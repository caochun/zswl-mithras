package cn.zswltech.mithras.service.flow.listener.endhandler.contract;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessCollectionManualRecordREQ;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractSpecialTradeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessState;
import cn.zswltech.mithras.payment.domain.enums.PaymentMethod;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingRepayActualService;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.PenaltyReduceBaseInfo;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.PenaltyReduceDetailRecord;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPrepayment;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.afterlese.PenaltyReduceBaseInfoService;
import cn.zswltech.mithras.afterlease.application.PenaltyReduceDetailRecordService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionFlowCenterService;
import cn.zswltech.mithras.contract.core.application.evaluation.ContractEvaluationAgencyDraftService;
import cn.zswltech.mithras.contract.core.application.ContractPrepaymentService;
import cn.zswltech.mithras.service.service.contract.ContractService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;

/**
 * 合同变更
 *
 * @author wangchuanhao
 * @date 2022/12/15 11:02 AM
 */
@Slf4j
@Component
public class ContractModifyProcessEndWorker extends AbstractContractProcessEndWorker {
    @Resource
    private ContractPrepaymentService contractPrepaymentService;
    @Resource
    private ContractService contractService;
    @Resource
    private ContractEvaluationAgencyDraftService contractEvaluationAgencyDraftService;
    @Resource
    private FundDirectFinancingRepayActualService directFinancingRepayActualService;

    @Override
    public void customProcessPass(String modelKey, Long contractId, Integer endType, Long startUserId, String processInstanceId) {
//        // 部分还款、调整还款计划、调整LPR、展期需要进行收益分摊明细变更
//        if (CharSequenceUtil.equalsAny(modelKey,
//                ProcessModelTypeEnum.ContractEarlyRepayFlow.name(),
//                ProcessModelTypeEnum.ContractChangeRepayPlanFlow.name(),
//                ProcessModelTypeEnum.ContractLPRChangeFlow.name(),
//                ProcessModelTypeEnum.ContractExtensionFlow.name()
//        )) {
//            // 异步执行收入确认分摊逻辑
//            this.doIncomeSharing(modelKey, contractId);
//        }
        // 异步执行收入确认分摊逻辑，具体是否要做变更由内部方法根据变更标识来判断，此处不判断流程类型
        this.doIncomeSharing(modelKey, contractId);
        // 更新对应的现金流
        directFinancingRepayActualService.updateCashFlow(contractId);
        // 抄送租赁物的评估机构到生效区
        contractEvaluationAgencyDraftService.copyDraft2Effect(contractId);
    }

    @Override
    public List<ProcessModelTypeEnum> handleModelTypeList() {
        return ListUtil.toList(ProcessModelTypeEnum.ContractModifyFlow,
                ProcessModelTypeEnum.ContractLPRChangeFlow,
                ProcessModelTypeEnum.ContractExtensionFlow,
                ProcessModelTypeEnum.ContractEarlyRepayFlow,
                ProcessModelTypeEnum.ContractChangeRepayPlanFlow);
    }

    @Override
    public ContractStatus getContractStatus(String modelKey, boolean processPass) {
        return null;
    }

    @Override
    public ContractProcessStatusEnum getContractProcessStatus(String modelKey, boolean processPass) {
        return processPass ? ContractProcessStatusEnum.CHANGE_PASS : ContractProcessStatusEnum.CHANGE_CANCEL;
    }

    @Override
    public void recordStatus(String modelKey, Long contractId, boolean processPass) {
        super.recordStatus(modelKey, contractId, processPass);
        // 清除流程子类型数据
        if (processPass) {
            contractBaseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_PASS.name(), null, contractId);
        }
    }

    @Override
    public void afterAllHook(String modelKey, Long contractId, Integer endType, Long startUserId, String processInstanceId) {
        if (ProcessModelTypeEnum.ContractEarlyRepayFlow.name().equals(modelKey)) {
            ContractPrepayment contractPrepayment = contractPrepaymentService.getList(new ContractIdListREQ(contractId));
            if (ProcessBusinessStatusEnum.success(endType) && Objects.nonNull(contractPrepayment) && Objects.equals(contractPrepayment.getIsEarlySettle(), YesOrNoNumberEnum.YES.getCode())) {
                // 提前结清 记录特殊交易 报送
                contractService.recordSpecialTrade(contractId, ContractSpecialTradeTypeEnum.SETTLE_IN_ADVANCE, processInstanceId);
                // 处理保证金
                this.doEarnestMoney(contractPrepayment);
            }
            if (ProcessBusinessStatusEnum.success(endType)) {
                // 处理违约金
                this.doPenalty(processInstanceId, contractPrepayment);
            }
            //提前还款流程后清空数据
            contractPrepaymentService.remove(Wrappers.<ContractPrepayment>lambdaQuery().eq(ContractPrepayment::getContractId, contractId));
        }
        if (ProcessModelTypeEnum.ContractExtensionFlow.name().equals(modelKey) && ProcessBusinessStatusEnum.success(endType)) {
            // 展期 记录特殊交易
            contractService.recordSpecialTrade(contractId, ContractSpecialTradeTypeEnum.CHANGE_EXTENSION, processInstanceId);
        }
    }

    @Override
    public void notifyCollection(String modelKey, Long contractId) {
        if (ProcessModelTypeEnum.ContractEarlyRepayFlow.name().equals(modelKey)) {
            contractService.notifyOnPrepayment(contractBaseInfoService.getById(contractId));
        }
        super.notifyCollection(modelKey, contractId);
    }

    private void doEarnestMoney(ContractPrepayment contractPrepayment) {
        if (Objects.equals(contractPrepayment.getIsEarnestMoneyDeduction(), YesOrNoNumberEnum.NO.getCode())) {
            return;
        }
        // 处理保证金抵扣
        long amount = Optional.ofNullable(contractPrepayment.getEarnestMoneyDeductionAmount()).orElse(0L);
        if (amount <= 0) {
            return;
        }
        // 找到所有未核销完毕的租金
        LambdaQueryWrapper<CollectionBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(CollectionBaseInfo::getContractId, contractPrepayment.getContractId());
        query.eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name());
        query.gt(CollectionBaseInfo::getPhase, 0);
        query.ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
        List<CollectionBaseInfo> collectionBaseInfoList = SpringUtil.getBean(CollectionBaseInfoService.class).list(query);
        if (CollectionUtil.isEmpty(collectionBaseInfoList)) {
            return;
        }
        // 根据应收日期排序
        collectionBaseInfoList.sort(Comparator.comparing(CollectionBaseInfo::getPlanCollectionDate));
        // 复用手动核销逻辑
        for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
            if (amount <= 0) {
                break;
            }
            // 剩余可核销本金
            long planPrincipal = Optional.ofNullable(collectionBaseInfo.getPrincipal()).orElse(0L) - Optional.ofNullable(collectionBaseInfo.getCollectionPrincipal()).orElse(0L);
            // 剩余可核销利息
            long planInterest = Optional.ofNullable(collectionBaseInfo.getInterest()).orElse(0L) - Optional.ofNullable(collectionBaseInfo.getCollectionInterest()).orElse(0L);
            long actualPrincipal = 0L;
            long actualInterest = 0L;
            if (planPrincipal > 0 && amount > 0) {
                actualPrincipal = Math.min(amount, planPrincipal);
                amount = amount - actualPrincipal;
            }
            if (planInterest > 0 && amount > 0) {
                actualInterest = Math.min(amount, planInterest);
                amount = amount - actualInterest;
            }
            // 复用手工核销的保证金抵扣方法
            CollectionFlowCenterBusinessCollectionManualRecordREQ req = new CollectionFlowCenterBusinessCollectionManualRecordREQ();
            req.setCollectionId(collectionBaseInfo.getId().toString());
            req.setCollectionCode(collectionBaseInfo.getCode());
            req.setCashFlowItem(collectionBaseInfo.getCashFlowItem());
            req.setCollectionType(PaymentMethod.REFUND_MARGIN_DEDUCT.display());
            req.setCollectionDate(contractPrepayment.getApplayRepaymentDate());
            req.setPrincipal(actualPrincipal);
            req.setInterest(actualInterest);
            req.setCollectionAmount(actualPrincipal + actualInterest);
            SpringUtil.getBean(CollectionFlowCenterService.class).collectionManualRecord(req, true);
        }
    }

    private void doPenalty(String processInstanceId, ContractPrepayment contractPrepayment) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractPrepayment.getContractId());
        long penaltyDerateAmount = Optional.ofNullable(contractPrepayment.getPenaltyDerateAmount()).orElse(0L);
        if (penaltyDerateAmount <= 0) {
            return;
        }
        // 找到所有存在罚息的期项
        LambdaQueryWrapper<CollectionBaseInfo> collectionQuery = Wrappers.lambdaQuery();
        collectionQuery.eq(CollectionBaseInfo::getContractId, contractPrepayment.getContractId());
        collectionQuery.eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name());
        collectionQuery.gt(CollectionBaseInfo::getPhase, 0);
        collectionQuery.gt(CollectionBaseInfo::getPenaltyInterest, 0L);
        collectionQuery.ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
        List<CollectionBaseInfo> collectionBaseInfoList = SpringUtil.getBean(CollectionBaseInfoService.class).list(collectionQuery);
        if (CollectionUtil.isEmpty(collectionBaseInfoList)) {
            return;
        }
        // 罚息减免复用已有罚息减免的底层数据表，区别是此处生成的数据直接生效（为了能有统一的地方查看罚息减免的数据）
        // 罚息减免主表
        PenaltyReduceBaseInfo penaltyReduceBaseInfo = new PenaltyReduceBaseInfo();
        penaltyReduceBaseInfo.setNotes("合同提前还款流程<" + processInstanceId + ">申请罚息减免，说明：" + contractPrepayment.getRemark());
        penaltyReduceBaseInfo.setPenaltyReduceStatus(ProcessState.PASS.name());
        SpringUtil.getBean(PenaltyReduceBaseInfoService.class).save(penaltyReduceBaseInfo);
        // 罚息减免子表
        List<PenaltyReduceDetailRecord> recordList = new LinkedList<>();
        List<Long> updateCollectionIds = new LinkedList<>();
        collectionBaseInfoList.sort(Comparator.comparing(CollectionBaseInfo::getPlanCollectionDate));
        for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
            if (penaltyDerateAmount <= 0) {
                break;
            }
            long planCollectPenalty = Optional.ofNullable(collectionBaseInfo.getPenaltyInterest()).orElse(0L);
            long actualCollectPenalty = Optional.ofNullable(collectionBaseInfo.getCollectionPenaltyInterest()).orElse(0L);
            if (actualCollectPenalty >= planCollectPenalty) {
                continue;
            }
            long canDerateAmount = planCollectPenalty - actualCollectPenalty;
            long actualDerateAmount = Math.min(canDerateAmount, penaltyDerateAmount);
            recordList.add(this.buildFrom(penaltyReduceBaseInfo.getId(), collectionBaseInfo, contractBaseInfo, actualDerateAmount));
            penaltyDerateAmount = penaltyDerateAmount - actualDerateAmount;
        }
        if (CollectionUtil.isNotEmpty(recordList)) {
            SpringUtil.getBean(PenaltyReduceDetailRecordService.class).saveBatch(recordList);
        }
        if (CollectionUtil.isNotEmpty(updateCollectionIds)) {
            // 更新收款表状态
            LambdaUpdateWrapper<CollectionBaseInfo> collectionBaseInfoLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            collectionBaseInfoLambdaUpdateWrapper.set(CollectionBaseInfo::getPenaltyInterestCalculateFlag, YesOrNoNumberEnum.YES.getCode());
            collectionBaseInfoLambdaUpdateWrapper.in(CollectionBaseInfo::getId, updateCollectionIds);
            SpringUtil.getBean(CollectionBaseInfoService.class).update(null, collectionBaseInfoLambdaUpdateWrapper);
        }
        // 执行罚息减免（复用罚息减免流程的逻辑）
        SpringUtil.getBean(PenaltyReduceBaseInfoService.class).doPenaltyReduce(penaltyReduceBaseInfo.getId());
    }

    private PenaltyReduceDetailRecord buildFrom(Long baseInfoId, CollectionBaseInfo collectionBaseInfo, ContractBaseInfo contractBaseInfo, Long actualDerateAmount) {
        PenaltyReduceDetailRecord reduceDetailRecord = new PenaltyReduceDetailRecord();
        reduceDetailRecord.setReduceBaseId(baseInfoId);
        reduceDetailRecord.setClientId(collectionBaseInfo.getClientId());
        reduceDetailRecord.setContractId(collectionBaseInfo.getContractId());
        reduceDetailRecord.setCollectionId(collectionBaseInfo.getId());
        reduceDetailRecord.setContractCode(collectionBaseInfo.getContractCode());
        reduceDetailRecord.setReceiptId(collectionBaseInfo.getReceiptId());
        reduceDetailRecord.setReceiptCode(collectionBaseInfo.getReceiptCode());
        reduceDetailRecord.setPhase(collectionBaseInfo.getPhase());
        reduceDetailRecord.setApplyCreditAmount(contractBaseInfo.getApplyCreditAmount());
        reduceDetailRecord.setPlanCollectionAmount(collectionBaseInfo.getPlanCollectionAmount());
        reduceDetailRecord.setPlanCollectionDate(collectionBaseInfo.getPlanCollectionDate());
        reduceDetailRecord.setCollectionAmount(collectionBaseInfo.getCollectionAmount());
        reduceDetailRecord.setPenaltyCloseDate(LocalDate.now().plusDays(-1));
        reduceDetailRecord.setPenaltyInterest(collectionBaseInfo.getPenaltyInterest());
        reduceDetailRecord.setReducePenaltyInterest(actualDerateAmount);
        return reduceDetailRecord;
    }
}
