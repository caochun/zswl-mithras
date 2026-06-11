package cn.zswltech.mithras.ftp.newftp.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.newftp.FtpAssessInfo;
import cn.zswltech.mithras.dto.newftp.NewFtpInterestChangeApplyRecordRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpInterestChangeApplySaveREQ;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.payment.enums.PaymentStatusEnum;
import cn.zswltech.mithras.contract.model.contract.ContractReceipt;
import cn.zswltech.mithras.payment.mapper.FtpAssessmentInfoMapper;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.model.FtpAssessmentInfo;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.contract.core.ContractReceiptService;
import cn.zswltech.mithras.ftp.newftp.mapper.NewFtpChangeApplyRecordMapper;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpChangeApplyRecord;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/3/4
 * @description
 */
@Service
public class NewFtpChangeApplyRecordService extends ServiceImpl<NewFtpChangeApplyRecordMapper, NewFtpChangeApplyRecord> {
    @Resource
    private FtpAssessmentInfoMapper ftpAssessmentInfoMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private SysUserService sysUserService;

    public NewFtpInterestChangeApplyRecordRSP detail(Long applyId) {
        NewFtpChangeApplyRecord record;
        NewFtpInterestChangeApplyRecordRSP rsp = new NewFtpInterestChangeApplyRecordRSP();
        if (Objects.nonNull(applyId)) {
            // 明确指定查询某一个申请数据，审批流详情中使用
            record = this.getById(applyId);
            if (Objects.isNull(record)) {
                throw new MithrasException("FTP计息申请记录不存在");
            }
            rsp.setApplyUserId(record.getApplyUserId());
        } else {
            Long currentUserId = AccountUtil.getLoginInfo().getId();
            LambdaQueryWrapper<NewFtpChangeApplyRecord> applyRecordQuery = Wrappers.lambdaQuery();
            applyRecordQuery.eq(NewFtpChangeApplyRecord::getApplyUserId, currentUserId);
            applyRecordQuery.eq(NewFtpChangeApplyRecord::getApprovalStatus, ProcessStatus.UN_SUBMIT.name());
            applyRecordQuery.orderByDesc(NewFtpChangeApplyRecord::getId);
            applyRecordQuery.last(StringUtil.mysqlLimitOne());
            record = this.getOne(applyRecordQuery);
            rsp.setApplyUserId(currentUserId);
        }
        rsp.setApplyUserName(id2NameService.sysUserId2NameSingle(rsp.getApplyUserId()));
        if (Objects.nonNull(record)) {
            rsp.setId(record.getId());
            // 填充FTP信息
            List<FtpAssessmentInfo> ftpAssessmentInfoList = listFtpAssessmentByApplyId(record.getId());
            if (CollectionUtil.isNotEmpty(ftpAssessmentInfoList)) {
                List<PaymentBaseInfo> paymentBaseInfoList = listPaymentByReceiptIds(ftpAssessmentInfoList.stream().map(FtpAssessmentInfo::getReceiptId).collect(Collectors.toList()));
                paymentBaseInfoList.removeIf(e -> !StrUtil.equalsAny(e.getPaymentStatus(), PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name()));
                Map<Long, List<PaymentBaseInfo>> paymentBaseInfoMap = paymentBaseInfoList.stream().collect(Collectors.groupingBy(PaymentBaseInfo::getReceiptId));
                List<FtpAssessInfo> ftpAssessInfoList = ftpAssessmentInfoList.stream().map(dbResult -> {
                    FtpAssessInfo item = BeanUtil.copyProperties(dbResult, FtpAssessInfo.class);
                    List<PaymentBaseInfo> plist = paymentBaseInfoMap.get(dbResult.getReceiptId());
                    if (CollectionUtil.isNotEmpty(plist)) {
                        PaymentBaseInfo paymentBaseInfo = plist.get(0);
                        item.setClientId(paymentBaseInfo.getClientId());
                        item.setClientName(id2NameService.clientId2NameSingle(paymentBaseInfo.getClientId()));
                        item.setContractId(paymentBaseInfo.getContractId());
                        item.setContractCode(paymentBaseInfo.getContractCode());
                        item.setReceiptId(paymentBaseInfo.getReceiptId());
                        item.setReceiptCode(paymentBaseInfo.getReceiptCode());
                    }
                    return item;
                }).collect(Collectors.toList());
                rsp.setFtpAssessmentInfoList(ftpAssessInfoList);
            }
        }
        return rsp;
    }

    @Transactional(rollbackFor = Throwable.class)
    public Long save(NewFtpInterestChangeApplySaveREQ req) {
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        Long ftpInterestChangeApplyRecordId = req.getId();
        if (Objects.isNull(ftpInterestChangeApplyRecordId)) {
            // 新增
            NewFtpChangeApplyRecord newFtpChangeApplyRecord = new NewFtpChangeApplyRecord();
            newFtpChangeApplyRecord.setApplyUserId(currentUserId);
            newFtpChangeApplyRecord.setApprovalStatus(ProcessStatus.UN_SUBMIT.name());
            this.save(newFtpChangeApplyRecord);
            ftpInterestChangeApplyRecordId = newFtpChangeApplyRecord.getId();
        } else {
            NewFtpChangeApplyRecord record = this.getById(ftpInterestChangeApplyRecordId);
            if (Objects.isNull(record)) {
                throw new MithrasException("FTP计息变更记录不存在");
            }
            if (StrUtil.equalsAny(record.getApprovalStatus(), ProcessStatus.APPROVAL_PASS.name())) {
                throw new MithrasException("审批通过的申请不允许修改");
            }
            // 移除所有老的归属于本次申请的FTP价格信息
            removeFtpAssessmentByApplyId(ftpInterestChangeApplyRecordId);
        }
        if (CollectionUtil.isNotEmpty(req.getFtpAssessmentInfoList())) {
            List<Long> receiptIds = req.getFtpAssessmentInfoList().stream().map(NewFtpInterestChangeApplySaveREQ.FtpAssessInfo::getReceiptId).collect(Collectors.toList());
            List<PaymentBaseInfo> paymentBaseInfoList = listPaymentByReceiptIds(receiptIds);
            paymentBaseInfoList.removeIf(e -> !StrUtil.equalsAny(e.getPaymentStatus(), PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name()));
            Map<Long, List<PaymentBaseInfo>> paymentBaseInfoMap = paymentBaseInfoList.stream().collect(Collectors.groupingBy(PaymentBaseInfo::getReceiptId));
            List<FtpAssessmentInfo> todoList = new ArrayList<>(req.getFtpAssessmentInfoList().size());
            for (NewFtpInterestChangeApplySaveREQ.FtpAssessInfo reqFtpAssessInfo : req.getFtpAssessmentInfoList()) {
                List<PaymentBaseInfo> pbiList = paymentBaseInfoMap.get(reqFtpAssessInfo.getReceiptId());
                pbiList.sort(Comparator.comparing(PaymentBaseInfo::getId));
                PaymentBaseInfo paymentBaseInfo = pbiList.get(0);
                if (Objects.isNull(paymentBaseInfo)) {
                    ContractReceipt contractReceipt = SpringUtil.getBean(ContractReceiptService.class).getById(reqFtpAssessInfo.getReceiptId());
                    throw new MithrasException(String.format("编号为%s的借据没有关联任何生效的付款申请", contractReceipt.getReceiptCode()));
                }
                FtpAssessmentInfo ftpAssessmentInfo = BeanUtil.copyProperties(reqFtpAssessInfo, FtpAssessmentInfo.class);
                ftpAssessmentInfo.setFtpInterestChangeApplyRecordId(ftpInterestChangeApplyRecordId);
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
        return ftpInterestChangeApplyRecordId;
    }

    @Transactional(rollbackFor = Throwable.class)
    public String submit(Long id) {
        // 校验数据
        List<FtpAssessmentInfo> ftpAssessmentInfoList = listFtpAssessmentByApplyId(id);
        if (CollectionUtil.isEmpty(ftpAssessmentInfoList)) {
            throw new MithrasException("至少需要存在一条FTP考核信息");
        }
        // 修改审批状态
        NewFtpChangeApplyRecord record = this.getById(id);
        if (Objects.isNull(record)) {
            throw new MithrasException("FTP计息申请记录不存在");
        }
        record.setApprovalStatus(ProcessStatus.UNDER_APPROVAL.name());
        this.updateById(record);
        // 起流程
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.FtpInterestChangeApplyFlow.name());
        startProcessReq.setBusinessKey(id.toString());
        startProcessReq.setProcessInstanceName("FTP计息变更");
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        startProcessReq.setStartUserId(currentUserId.toString());
        // 找一下部门
        List<OrgDO> orgList = sysUserService.getSpecificUserDeptList(currentUserId);
        if (CollectionUtil.isNotEmpty(orgList)) {
            startProcessReq.setStartUserDeptId(orgList.get(0).getId().toString());
        }
        return flowProcessApiService.start(startProcessReq);
    }

    public void modifyApprovalStatus(Long id, ProcessStatus processStatus) {
        LambdaUpdateWrapper<NewFtpChangeApplyRecord> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(NewFtpChangeApplyRecord::getApprovalStatus, processStatus.name());
        updateWrapper.eq(NewFtpChangeApplyRecord::getId, id);
        this.update(updateWrapper);
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

    private List<PaymentBaseInfo> listPaymentByReceiptIds(List<Long> receiptIds) {
        if (CollectionUtil.isEmpty(receiptIds)) {
            return Collections.emptyList();
        }
        return paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(PaymentBaseInfo::getReceiptId, receiptIds));
    }
}
