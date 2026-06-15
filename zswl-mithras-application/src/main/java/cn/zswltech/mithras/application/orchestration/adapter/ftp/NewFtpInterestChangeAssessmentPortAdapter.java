package cn.zswltech.mithras.application.orchestration.adapter.ftp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.contract.core.ContractReceiptService;
import cn.zswltech.mithras.contract.model.contract.ContractReceipt;
import cn.zswltech.mithras.dto.newftp.FtpAssessInfo;
import cn.zswltech.mithras.dto.newftp.NewFtpInterestChangeApplySaveREQ;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.ClientNameResolver;
import cn.zswltech.mithras.ftp.newftp.service.port.NewFtpInterestChangeAssessmentPort;
import cn.zswltech.mithras.payment.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.mapper.FtpAssessmentInfoMapper;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.model.FtpAssessmentInfo;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class NewFtpInterestChangeAssessmentPortAdapter implements NewFtpInterestChangeAssessmentPort {

    @Resource
    private FtpAssessmentInfoMapper ftpAssessmentInfoMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ClientNameResolver clientNameResolver;
    @Resource
    private ContractReceiptService contractReceiptService;

    @Override
    public List<FtpAssessInfo> listByApplyId(Long applyId) {
        List<FtpAssessmentInfo> ftpAssessmentInfoList = listFtpAssessmentByApplyId(applyId);
        if (CollectionUtil.isEmpty(ftpAssessmentInfoList)) {
            return Collections.emptyList();
        }
        List<PaymentBaseInfo> paymentBaseInfoList = listEffectivePaymentByReceiptIds(
                ftpAssessmentInfoList.stream().map(FtpAssessmentInfo::getReceiptId).collect(Collectors.toList()));
        Map<Long, List<PaymentBaseInfo>> paymentBaseInfoMap = paymentBaseInfoList.stream()
                .collect(Collectors.groupingBy(PaymentBaseInfo::getReceiptId));
        return ftpAssessmentInfoList.stream().map(dbResult -> {
            FtpAssessInfo item = BeanUtil.copyProperties(dbResult, FtpAssessInfo.class);
            List<PaymentBaseInfo> plist = paymentBaseInfoMap.get(dbResult.getReceiptId());
            if (CollectionUtil.isNotEmpty(plist)) {
                PaymentBaseInfo paymentBaseInfo = plist.get(0);
                item.setClientId(paymentBaseInfo.getClientId());
                item.setClientName(clientNameResolver.clientId2NameSingle(paymentBaseInfo.getClientId()));
                item.setContractId(paymentBaseInfo.getContractId());
                item.setContractCode(paymentBaseInfo.getContractCode());
                item.setReceiptId(paymentBaseInfo.getReceiptId());
                item.setReceiptCode(paymentBaseInfo.getReceiptCode());
            }
            return item;
        }).collect(Collectors.toList());
    }

    @Override
    public void replaceByApplyId(Long applyId, List<NewFtpInterestChangeApplySaveREQ.FtpAssessInfo> assessmentInfoList) {
        removeFtpAssessmentByApplyId(applyId);
        if (CollectionUtil.isEmpty(assessmentInfoList)) {
            return;
        }
        List<Long> receiptIds = assessmentInfoList.stream()
                .map(NewFtpInterestChangeApplySaveREQ.FtpAssessInfo::getReceiptId)
                .collect(Collectors.toList());
        List<PaymentBaseInfo> paymentBaseInfoList = listEffectivePaymentByReceiptIds(receiptIds);
        Map<Long, List<PaymentBaseInfo>> paymentBaseInfoMap = paymentBaseInfoList.stream()
                .collect(Collectors.groupingBy(PaymentBaseInfo::getReceiptId));
        List<FtpAssessmentInfo> todoList = new ArrayList<>(assessmentInfoList.size());
        for (NewFtpInterestChangeApplySaveREQ.FtpAssessInfo reqFtpAssessInfo : assessmentInfoList) {
            List<PaymentBaseInfo> pbiList = paymentBaseInfoMap.get(reqFtpAssessInfo.getReceiptId());
            if (CollectionUtil.isEmpty(pbiList)) {
                ContractReceipt contractReceipt = contractReceiptService.getById(reqFtpAssessInfo.getReceiptId());
                throw new MithrasException(String.format("编号为%s的借据没有关联任何生效的付款申请",
                        Objects.isNull(contractReceipt) ? reqFtpAssessInfo.getReceiptId() : contractReceipt.getReceiptCode()));
            }
            pbiList.sort(Comparator.comparing(PaymentBaseInfo::getId));
            PaymentBaseInfo paymentBaseInfo = pbiList.get(0);
            FtpAssessmentInfo ftpAssessmentInfo = BeanUtil.copyProperties(reqFtpAssessInfo, FtpAssessmentInfo.class);
            ftpAssessmentInfo.setFtpInterestChangeApplyRecordId(applyId);
            ftpAssessmentInfo.setPaymentId(paymentBaseInfo.getId());
            ftpAssessmentInfo.setIsEffect(YesOrNoNumberEnum.NO.getCode());
            ftpAssessmentInfo.setGuidePrice();
            ftpAssessmentInfo.setAssessmentPrice();
            todoList.add(ftpAssessmentInfo);
        }
        if (CollectionUtil.isNotEmpty(todoList)) {
            todoList.forEach(ftpAssessmentInfoMapper::insert);
        }
    }

    @Override
    public boolean existsByApplyId(Long applyId) {
        return CollectionUtil.isNotEmpty(listFtpAssessmentByApplyId(applyId));
    }

    private List<FtpAssessmentInfo> listFtpAssessmentByApplyId(Long applyId) {
        return ftpAssessmentInfoMapper.selectList(Wrappers.<FtpAssessmentInfo>lambdaQuery()
                .eq(FtpAssessmentInfo::getFtpInterestChangeApplyRecordId, applyId));
    }

    private void removeFtpAssessmentByApplyId(Long applyId) {
        List<FtpAssessmentInfo> list = listFtpAssessmentByApplyId(applyId);
        if (CollectionUtil.isNotEmpty(list)) {
            list.stream().map(FtpAssessmentInfo::getId).forEach(ftpAssessmentInfoMapper::deleteById);
        }
    }

    private List<PaymentBaseInfo> listEffectivePaymentByReceiptIds(List<Long> receiptIds) {
        if (CollectionUtil.isEmpty(receiptIds)) {
            return Collections.emptyList();
        }
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(PaymentBaseInfo::getReceiptId, receiptIds));
        paymentBaseInfoList.removeIf(e -> !StrUtil.equalsAny(e.getPaymentStatus(),
                PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name()));
        return paymentBaseInfoList;
    }
}
