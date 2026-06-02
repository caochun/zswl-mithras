package cn.zswltech.mithras.service.service.payment;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentStatusEnum;
import cn.zswltech.mithras.service.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.ftp.bo.BillFtpBO;
import cn.zswltech.mithras.ftp.bo.CashFtpInfluenceBO;
import cn.zswltech.mithras.service.service.contract.ContractReceiptService;
import cn.zswltech.mithras.service.service.newftp.service.FtpService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.zswltech.mithras.service.mapper.model.payment.FtpAssessmentInfo;
import cn.zswltech.mithras.service.mapper.payment.FtpAssessmentInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @description 针对表【ftp_assessment_info(FTP考核信息表)】的数据库操作Service实现
 * @createDate 2024-08-09 10:40:12
 */
@Slf4j
@Service
public class FtpAssessmentInfoService extends ServiceImpl<FtpAssessmentInfoMapper, FtpAssessmentInfo> {
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private FtpService ftpService;
    @Resource
    private ContractReceiptService contractReceiptService;

    public FtpAssessmentInfo findFirstByPaymentId(Long paymentId) {
        LambdaQueryWrapper<FtpAssessmentInfo> query = Wrappers.lambdaQuery();
        query.eq(FtpAssessmentInfo::getPaymentId, paymentId);
        query.eq(FtpAssessmentInfo::getFtpInterestChangeApplyRecordId, 0);
        return this.getOne(query);
    }

    public void initByReceiptId(Long receiptId) {
        FtpAssessmentInfo ftpAssessmentInfo = new FtpAssessmentInfo();
        ftpAssessmentInfo.setReceiptId(receiptId);
        ftpAssessmentInfo.setPaymentId(0L);
        this.save(ftpAssessmentInfo);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void tryRecalculateFtpAssessmentInfo(Long receiptId) {
        ContractReceipt contractReceipt = contractReceiptService.getById(receiptId);
        if (Objects.isNull(contractReceipt)) {
            log.error("FTP考核价格信息表-通过借据id没有找到借据信息[借据id:{}]", receiptId);
            return;
        }
        List<FtpAssessmentInfo> ftpAssessmentInfoList = this.listAllByReceiptId(receiptId);
        // 生效的不用重算
        ftpAssessmentInfoList.removeIf(e -> Objects.equals(e.getIsEffect(), YesOrNoNumberEnum.YES.getCode()));
        // 走FTP计息变更申请的也不用
        ftpAssessmentInfoList.removeIf(e -> (Objects.nonNull(e.getFtpInterestChangeApplyRecordId()) && e.getFtpInterestChangeApplyRecordId() > 0));
        if (CollectionUtil.isEmpty(ftpAssessmentInfoList)) {
            return;
        }
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.listByReceiptId(receiptId);
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return;
        }
        LocalDate ftpTargetDate;
        Long paymentId;
        List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailService.listByPaymentIds(paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList()));
        if (CollectionUtil.isEmpty(paymentActualDetailList)) {
            paymentBaseInfoList.sort(Comparator.comparing(PaymentBaseInfo::getId));
            ftpTargetDate = LocalDate.now();
            paymentId = paymentBaseInfoList.get(0).getId();
        } else {
            paymentActualDetailList.sort(Comparator.comparing(PaymentActualDetail::getPaidInDate));
            // 取到最早的实际付款日期
            PaymentActualDetail first = paymentActualDetailList.get(0);
            ftpTargetDate = first.getPaidInDate();
            paymentId = first.getPaymentId();
        }
        // 填充FTP价格信息
        final Long pid = paymentId;
        ftpAssessmentInfoList.forEach(e -> {
            CashFtpInfluenceBO cashFtpInfluenceBO = ftpService.getCashFtpInfluence(contractReceipt.getContractId(), ftpTargetDate);
            cashFtpInfluenceBO.setReceiptId(contractReceipt.getId());
            Integer cashFtp = ftpService.getCashFtp(cashFtpInfluenceBO, null);
            BillFtpBO billFtpBO = ftpService.getBillFtp(ftpTargetDate);
            if (Objects.nonNull(pid)) {
                e.setPaymentId(pid);
            }
            if (Objects.nonNull(cashFtp)) {
                e.setBasePrice(Long.valueOf(cashFtp));
            }
            if (Objects.nonNull(billFtpBO) && Objects.nonNull(billFtpBO.getBillFtpSell())) {
                e.setTicketPrice(Long.valueOf(billFtpBO.getBillFtpSell()));
            }
            e.setGuidePrice();
            e.setAssessmentPrice();
        });
        this.updateBatchById(ftpAssessmentInfoList);
    }

    public void effectByPaymentReceiptId(Long paymentId, Long receiptId) {
        LambdaUpdateWrapper<FtpAssessmentInfo> updateWrapper = Wrappers.lambdaUpdate();
        if (Objects.isNull(paymentId)) {
            // 上层没有指定就尝试找一下，找到多个的话就取最早的那个
            List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.listByReceiptId(receiptId);
            if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
                throw new MithrasException("不存在找到借据关联的付款申请");
            }
            // 只取生效和已投放的
            paymentBaseInfoList.removeIf(e -> !StrUtil.equalsAny(e.getPaymentStatus(), PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name()));
            if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
                throw new MithrasException("没有找到借据关联的生效付款申请");
            }
            // 排序取最早
            paymentBaseInfoList.sort(Comparator.comparing(PaymentBaseInfo::getId));
            updateWrapper.set(FtpAssessmentInfo::getPaymentId, paymentBaseInfoList.get(0).getId());
        } else {
            // 上层指定付款id就用上层指定的
            updateWrapper.set(FtpAssessmentInfo::getPaymentId, paymentId);
        }
        updateWrapper.set(FtpAssessmentInfo::getIsEffect, YesOrNoNumberEnum.YES.getCode());
        // FTP计息变更申请添加的数据踢掉
        updateWrapper.eq(FtpAssessmentInfo::getFtpInterestChangeApplyRecordId, 0L);
        updateWrapper.eq(FtpAssessmentInfo::getReceiptId, receiptId);
        updateWrapper.eq(FtpAssessmentInfo::getIsEffect, YesOrNoNumberEnum.NO.getCode());
        this.update(updateWrapper);
    }

    public List<FtpAssessmentInfo> listByApplyId(Long ftpInterestChangeApplyId) {
        LambdaQueryWrapper<FtpAssessmentInfo> query = Wrappers.lambdaQuery();
        query.eq(FtpAssessmentInfo::getFtpInterestChangeApplyRecordId, ftpInterestChangeApplyId);
        return this.list(query);
    }

    public List<FtpAssessmentInfo> listEffectByApplyId(Long ftpInterestChangeApplyId) {
        LambdaQueryWrapper<FtpAssessmentInfo> query = Wrappers.lambdaQuery();
        query.eq(FtpAssessmentInfo::getFtpInterestChangeApplyRecordId, ftpInterestChangeApplyId);
        query.eq(FtpAssessmentInfo::getIsEffect, YesOrNoNumberEnum.YES.getCode());
        return this.list(query);
    }

    public void effectByApplyId(Long ftpInterestChangeApplyId) {
        LambdaUpdateWrapper<FtpAssessmentInfo> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(FtpAssessmentInfo::getIsEffect, YesOrNoNumberEnum.YES.getCode());
        updateWrapper.eq(FtpAssessmentInfo::getFtpInterestChangeApplyRecordId, ftpInterestChangeApplyId);
        updateWrapper.eq(FtpAssessmentInfo::getIsEffect, YesOrNoNumberEnum.NO.getCode());
        this.update(updateWrapper);
    }

    public void removeByApplyId(Long ftpInterestChangeApplyId) {
        List<FtpAssessmentInfo> list = this.listByApplyId(ftpInterestChangeApplyId);
        if (CollectionUtil.isNotEmpty(list)) {
            this.removeByIds(list.stream().map(FtpAssessmentInfo::getId).collect(Collectors.toSet()));
        }
    }

    public void removeByReceiptId(Long receiptId) {
        List<FtpAssessmentInfo> ftpAssessmentInfoList = this.listAllByReceiptId(receiptId);
        // 生效的不能删除
        ftpAssessmentInfoList.removeIf(e -> Objects.equals(e.getIsEffect(), YesOrNoNumberEnum.YES.getCode()));
        if (CollectionUtil.isNotEmpty(ftpAssessmentInfoList)) {
            this.removeByIds(ftpAssessmentInfoList.stream().map(FtpAssessmentInfo::getId).collect(Collectors.toSet()));
        }
    }

    public FtpAssessmentInfo findLatestEffect(LocalDate targetDate, Long receiptId) {
        LambdaQueryWrapper<FtpAssessmentInfo> query = Wrappers.lambdaQuery();
        query.eq(FtpAssessmentInfo::getReceiptId, receiptId);
        query.eq(FtpAssessmentInfo::getIsEffect, YesOrNoNumberEnum.YES.getCode());
        query.le(FtpAssessmentInfo::getEffectDate, targetDate);
        query.orderByDesc(FtpAssessmentInfo::getEffectDate);
        query.orderByDesc(FtpAssessmentInfo::getId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public List<FtpAssessmentInfo> listAllByReceiptId(Long receiptId) {
        LambdaQueryWrapper<FtpAssessmentInfo> query = Wrappers.lambdaQuery();
        query.eq(FtpAssessmentInfo::getReceiptId, receiptId);
        return this.list(query);
    }

    public void refreshEffectDate(Long paymentId, Long receiptId, LocalDate effectDate) {
        LambdaUpdateWrapper<FtpAssessmentInfo> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(FtpAssessmentInfo::getEffectDate, effectDate);
        updateWrapper.eq(FtpAssessmentInfo::getPaymentId, paymentId);
        updateWrapper.eq(FtpAssessmentInfo::getReceiptId, receiptId);
        this.update(updateWrapper);
    }
}




