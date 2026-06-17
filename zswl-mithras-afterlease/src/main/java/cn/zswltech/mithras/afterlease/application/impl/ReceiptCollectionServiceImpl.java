package cn.zswltech.mithras.afterlease.application.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.afterlease.*;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCollectionPort;
import cn.zswltech.mithras.afterlease.application.AfterLeaseContractApprovalContext;
import cn.zswltech.mithras.afterlease.application.AfterLeaseContractPort;
import cn.zswltech.mithras.afterlease.application.AfterLeaseMaterialsPort;
import cn.zswltech.mithras.afterlease.application.AfterLeasePenaltyCollection;
import cn.zswltech.mithras.afterlease.application.AfterLeaseReceiptCollectionSnapshot;
import cn.zswltech.mithras.afterlease.application.AfterLeaseSponsorAuthPort;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeasePenaltyReductionMaterialsEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseProcessEndResult;
import cn.zswltech.mithras.afterlease.enums.RentCollectionLevelEnum;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.afterlease.model.CollectionPenaltyReductionInfo;
import cn.zswltech.mithras.afterlease.model.CollectionPenaltyReductionRelation;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.afterlease.application.AfterLeaseWorkflowPort;
import cn.zswltech.mithras.afterlease.application.CollectionPenaltyReductionService;
import cn.zswltech.mithras.afterlease.application.impl.CollectionPenaltyReductionRelationService;
import cn.zswltech.mithras.afterlease.application.ReceiptCollectionService;
import cn.zswltech.mithras.afterlease.util.CollectionLevelUtil;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @ClassName ReceiptCollectionServiceImpl
 * @Author jackerhe
 * @Date 2022/11/19 4:51 下午
 * @Version 1.0
 **/
@Service
@Slf4j
public class ReceiptCollectionServiceImpl implements ReceiptCollectionService {

    private static final String BUSINESS_MODULE_CONTRACT = "CONTRACT";
    private static final String BUSINESS_MODULE_OVERDUE_COLLECTION_REDUCTION = "OVERDUE_COLLECTION_REDUCTION";

    @Resource
    private AfterLeaseCollectionPort afterLeaseCollectionPort;
    @Resource
    private CollectionPenaltyReductionService collectionPenaltyReductionService;
    @Resource
    private CollectionPenaltyReductionRelationService collectionPenaltyReductionRelationService;
    @Resource
    private AfterLeaseMaterialsPort afterLeaseMaterialsPort;
    @Resource
    private AfterLeaseContractPort afterLeaseContractPort;
    @Resource
    private AfterLeaseSponsorAuthPort afterLeaseSponsorAuthPort;
    @Resource
    private AfterLeaseWorkflowPort workflowPort;

    @Override
    public Page<AfterLeaseReceiptCollectionSnapshot> list(ReceiptCollectionListREQ req) {
        return afterLeaseCollectionPort.pageReceiptCollectionByPaymentCode(req.getPaymentCode(), req.getPage(), req.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void collectionNotice(Long reduceId) {
        CollectionPenaltyReductionInfo reductionInfo = collectionPenaltyReductionService.getById(reduceId);
        if (ObjectUtil.isNull(reductionInfo)) {
            log.info("ReceiptCollectionServiceImpl collectionNotice reduceId {} not fund", reduceId);
            return;
        }
        AfterLeaseContractApprovalContext contractContext = afterLeaseContractPort.getApprovalContextById(reductionInfo.getContractId());
        if (ObjectUtil.isNull(contractContext)) {
            log.info("ReceiptCollectionServiceImpl collectionNotice reduceId {} not fund contract", reduceId);
            return;
        }
        //判断是否合同主办 --系统调用不再判断
        //dataAuthCreatorRule.check(BusinessModuleEnum.CONTRACT, reductionInfo.getContractId());
        //已推送
        // 查询产生罚息且罚息未全部收款期限
        List<AfterLeasePenaltyCollection> collectionBaseInfos =
                afterLeaseCollectionPort.listUnpaidPenaltyRentByContractId(reductionInfo.getContractId());
        if (ObjectUtil.isEmpty(collectionBaseInfos)) {
            return;
        }
        //if(Boolean.FALSE.equals(noticeFinancialDecide(collectionBaseInfos))) throw new MithrasException("已通知财务系统！");
        //查询减免金额
        List<CollectionPenaltyReductionInfo> penaltyReductionInfos = collectionPenaltyReductionService.list(Wrappers.<CollectionPenaltyReductionInfo>lambdaQuery()
                .eq(CollectionPenaltyReductionInfo::getContractId, reductionInfo.getContractId())
                .eq(CollectionPenaltyReductionInfo::getCollectionStatus, RecordStatus.TAKE_EFFECT.name())
                .gt(CollectionPenaltyReductionInfo::getPenaltyInterestSurplusAmount, 0));
        if (ObjectUtil.isNull(collectionBaseInfos)) {
            return;
        }
        long reduceTemp;
        long surplusAmount;
        List<CollectionPenaltyReductionRelation> reductionRelations = new ArrayList<>();
        List<AfterLeasePenaltyCollection> sendCollection = new ArrayList<>();
        //计算减免
        if (ObjectUtil.isNotEmpty(penaltyReductionInfos)) {
            //依次处理减免金额
            for (CollectionPenaltyReductionInfo reduceInfo : penaltyReductionInfos) {
                //罚息减免剩余金额大于0
                if (reduceInfo.getPenaltyInterestSurplusAmount() > 0) {
                    for (AfterLeasePenaltyCollection baseInfo : collectionBaseInfos) {
                        //以往减免
                        reduceTemp = LongUtil.null2zero(baseInfo.getPenaltyInterestDeductionAmount());
                        //本期剩余 = 罚息 - 实收 - 减免
                        surplusAmount = LongUtil.null2zero(baseInfo.getPenaltyInterest()) - LongUtil.null2zero(baseInfo.getCollectionPenaltyInterest()) - reduceTemp;
                        if (surplusAmount > 0) {
                            //开始减免
                            if (reduceInfo.getPenaltyInterestSurplusAmount() > surplusAmount) {
                                //减免大于本期
                                reduceInfo.setPenaltyInterestSurplusAmount(reduceInfo.getPenaltyInterestSurplusAmount() - surplusAmount);
                                baseInfo.setPenaltyInterestDeductionAmount(LongUtil.null2zero(baseInfo.getPenaltyInterestDeductionAmount()) + surplusAmount);
                                reductionRelations.add(buildCollectionPenaltyReductionRelation(reduceInfo.getId(), baseInfo.getCode(), baseInfo.getPaymentCode(), surplusAmount));
                            } else {
                                //减免小于等于本期
                                baseInfo.setPenaltyInterestDeductionAmount(LongUtil.null2zero(baseInfo.getPenaltyInterestDeductionAmount()) + reduceInfo.getPenaltyInterestSurplusAmount());
                                reductionRelations.add(buildCollectionPenaltyReductionRelation(reduceInfo.getId(), baseInfo.getCode(), baseInfo.getPaymentCode(), reduceInfo.getPenaltyInterestSurplusAmount()));
                                reduceInfo.setPenaltyInterestSurplusAmount(0L);
                                baseInfo.setPlanPenaltyInterestDate(LocalDate.now());
                                //通知次数+1
                                baseInfo.setOverdueCollectionCount(LongUtil.null2zero(baseInfo.getOverdueCollectionCount()) + 1);
                                sendCollection.add(baseInfo);
                                //结束
                                break;
                            }
                            baseInfo.setPlanPenaltyInterestDate(LocalDate.now());
                            //通知次数+1
                            baseInfo.setOverdueCollectionCount(LongUtil.null2zero(baseInfo.getOverdueCollectionCount()) + 1);
                            sendCollection.add(baseInfo);
                        }
                    }
                }
            }
        } else {
            //无减免，全部通知
            for (AfterLeasePenaltyCollection baseInfo : collectionBaseInfos) {
                baseInfo.setPlanPenaltyInterestDate(LocalDate.now());
                //通知次数+1
                baseInfo.setOverdueCollectionCount(LongUtil.null2zero(baseInfo.getOverdueCollectionCount()) + 1);
                sendCollection.add(baseInfo);
            }
        }
        //批量保存
        if (ObjectUtil.isNotEmpty(sendCollection)) {
            afterLeaseCollectionPort.updatePenaltyCollections(sendCollection);
        }
        if (ObjectUtil.isNotEmpty(penaltyReductionInfos)) {
            collectionPenaltyReductionService.updateBatchById(penaltyReductionInfos);
        }
        if (ObjectUtil.isNotEmpty(reductionRelations)) {
            collectionPenaltyReductionRelationService.saveBatch(reductionRelations);
        }
        afterLeaseContractPort.markOverdueCollectionNotified(reductionInfo.getContractId());
        //通知苍穹
        //financialManagerService.collectionExec(sendCollection.get());
    }

    @Override
    public CollectionOverdueRSP overdue(CollectionOverdueREQ req) {
        //计算
        CollectionOverdueRSP rsp = new CollectionOverdueRSP();
        rsp.setCollectionLevel(getCollectionLevel(req.getContractId()));
        rsp.setFileList(afterLeaseMaterialsPort.list(BUSINESS_MODULE_OVERDUE_COLLECTION_REDUCTION, Collections.singletonList(AfterLeasePenaltyReductionMaterialsEnum.OVERDUE_COLLECTION.name()),
                Collections.singletonList(req.getContractId())).stream().map(base -> BeanUtil.copyProperties(base, FileListRSP.class)).collect(Collectors.toList()));
        return rsp;
    }

    private String getCollectionLevel(Long contractId) {
        LocalDate planCollectionDate = afterLeaseCollectionPort.findFirstUnpaidPenaltyPlanDate(contractId);
        if (ObjectUtil.isNull(planCollectionDate)) {
            return null;
        }
        return Optional.ofNullable(CollectionLevelUtil.getCollectionLevel(CollectionLevelUtil.getOverdueDay(planCollectionDate))).map(RentCollectionLevelEnum::display).orElse(null);
    }

    private CollectionPenaltyReductionRelation buildCollectionPenaltyReductionRelation(Long reduceId, String code, String paymentCode, Long reduceAmount) {
        CollectionPenaltyReductionRelation collectionPenaltyReductionRelation = new CollectionPenaltyReductionRelation();
        collectionPenaltyReductionRelation.setReduceId(reduceId);
        collectionPenaltyReductionRelation.setCode(code);
        collectionPenaltyReductionRelation.setPaymentCode(paymentCode);
        collectionPenaltyReductionRelation.setReduceAmount(reduceAmount);
        collectionPenaltyReductionRelation.setStatus(Long.valueOf(YesOrNoNumberEnum.YES.getCode()));
        return collectionPenaltyReductionRelation;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void effect(CollectionPenaltyReductionEffectREQ req) {
        AfterLeaseContractApprovalContext contractContext = afterLeaseContractPort.getApprovalContextById(req.getContractId());
        if (Objects.isNull(contractContext)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //合同下有未生效的不许提交
        if (collectionPenaltyReductionService.count(Wrappers.<CollectionPenaltyReductionInfo>lambdaQuery()
                .eq(CollectionPenaltyReductionInfo::getContractId, req.getContractId())
                .eq(CollectionPenaltyReductionInfo::getCollectionStatus, RecordStatus.NEW.name())) > 0) {
            throw new MithrasException("此合同罚息减免审批未结束！");
        }
        //判断是否合同主办
        afterLeaseSponsorAuthPort.check(BUSINESS_MODULE_CONTRACT, req.getContractId());
        CollectionPenaltyReductionInfo collectionPenaltyReductionInfo = BeanUtil.copyProperties(req, CollectionPenaltyReductionInfo.class);
        collectionPenaltyReductionInfo.setPenaltyInterestSurplusAmount(collectionPenaltyReductionInfo.getPenaltyInterestDeductionAmount());
        collectionPenaltyReductionInfo.setProcessStatus(ProcessStatus.UNDER_APPROVAL.name());
        collectionPenaltyReductionInfo.setCollectionStatus(RecordStatus.NEW.name());
        collectionPenaltyReductionService.save(collectionPenaltyReductionInfo);
        if (ObjectUtil.isNotEmpty(req.getFiles())) {
            req.getFiles().forEach(file -> afterLeaseMaterialsPort.add(file, collectionPenaltyReductionInfo.getId(), AfterLeasePenaltyReductionMaterialsEnum.DEDUCTION_INTEREST.name(), BUSINESS_MODULE_OVERDUE_COLLECTION_REDUCTION));
        }
        AfterLeaseWorkflowPort.PenaltyReductionApprovalStartContext context =
                new AfterLeaseWorkflowPort.PenaltyReductionApprovalStartContext();
        context.setReductionId(collectionPenaltyReductionInfo.getId());
        context.setClientId(contractContext.getClientId());
        context.setBizDeptId(contractContext.getBizDeptId());
        context.setBizDeptLeaderId(contractContext.getBizDeptLeaderId());
        context.setBizDivisionLeaderId(contractContext.getBizDivisionLeaderId());
        context.setBizType(contractContext.getBizType());
        context.setProjName(contractContext.getProjName());
        context.setProjCosponsorUserIds(contractContext.getProjCosponsorUserIds());
        context.setApplyCreditAmount(contractContext.getApplyCreditAmount());
        workflowPort.startPenaltyReductionApproval(context);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void modify(CollectionPenaltyReductionModifyREQ req) {
        CollectionPenaltyReductionInfo collectionPenaltyReductionInfo = collectionPenaltyReductionService.getById(req.getId());
        if (ObjectUtil.isNull(collectionPenaltyReductionInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (RecordStatus.TAKE_EFFECT.name().equals(collectionPenaltyReductionInfo.getCollectionStatus()) || RecordStatus.CLOSED.name().equals(collectionPenaltyReductionInfo.getCollectionStatus())) {
            throw new MithrasException("减免已完结！");
        }
        collectionPenaltyReductionInfo.setPenaltyInterestDeductionAmount(req.getPenaltyInterestDeductionAmount());
        collectionPenaltyReductionInfo.setPenaltyInterestSurplusAmount(collectionPenaltyReductionInfo.getPenaltyInterestDeductionAmount());
        collectionPenaltyReductionInfo.setReasonExplain(req.getReasonExplain());
        if (ObjectUtil.isNotEmpty(req.getFiles())) {
            req.getFiles().forEach(file -> afterLeaseMaterialsPort.add(file, collectionPenaltyReductionInfo.getId(), AfterLeasePenaltyReductionMaterialsEnum.DEDUCTION_INTEREST.name(), BUSINESS_MODULE_OVERDUE_COLLECTION_REDUCTION));
        }
        if (ObjectUtil.isNotEmpty(req.getRemoveFileIds())) {
            afterLeaseMaterialsPort.remove(req.getRemoveFileIds());
        }
        collectionPenaltyReductionService.updateById(collectionPenaltyReductionInfo);
    }

    @Override
    public Long calculationInterest(Long contractId) {
        return afterLeaseCollectionPort.sumRemainingPenaltyInterest(contractId);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void processEnd(Long id, AfterLeaseProcessEndResult endResult, Long startUserId, String processInstanceId) {
        if (endResult.pass()) {
            recordCollectionPenaltyReductionStatus(id, RecordStatus.TAKE_EFFECT, ProcessStatus.APPROVAL_PASS);
        } else {
            recordCollectionPenaltyReductionStatus(id, RecordStatus.CLOSED, ProcessStatus.APPROVAL_REJECT);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void recordCollectionPenaltyReductionStatus(Long id, RecordStatus reductionStatus,
                                                       ProcessStatus reductionProcessStatus) {
        if (id == null || (reductionStatus == null && reductionProcessStatus == null)) {
            return;
        }
        LambdaUpdateWrapper<CollectionPenaltyReductionInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CollectionPenaltyReductionInfo::getId, id);
        if (reductionStatus != null) {
            updateWrapper.set(CollectionPenaltyReductionInfo::getCollectionStatus, reductionStatus.name());
        }
        if (reductionProcessStatus != null) {
            updateWrapper.set(CollectionPenaltyReductionInfo::getProcessStatus, reductionProcessStatus.name());
        }
        updateWrapper.set(CollectionPenaltyReductionInfo::getUpdateTime, LocalDateTime.now());
        collectionPenaltyReductionService.update(null, updateWrapper);
    }

}
