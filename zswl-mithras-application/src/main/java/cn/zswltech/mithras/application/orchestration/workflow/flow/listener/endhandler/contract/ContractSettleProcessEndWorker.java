package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler.contract;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.collection.application.CollectionOverdueRecordInfoService;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.workflow.enums.CommonProcessPrepareStatus;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.workflow.persistence.model.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.application.orchestration.client.ProjClientRoleService;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.contract.application.ContractLeaseItemService;
import cn.zswltech.mithras.contract.core.ContractSettlePlanService;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import cn.zswltech.mithras.workflow.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.collection.application.financial.FinancialManagerService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 合同结清
 *
 * @author wangchuanhao
 * @date 2022/12/15 10:50 AM
 */
@Component
@Slf4j
public class ContractSettleProcessEndWorker extends AbstractContractProcessEndWorker {

    @Resource
    private ContractSettlePlanService contractSettlePlanService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private MarginBaseInfoService marginBaseInfoService;
    @Resource
    private FinancialManagerService financialManagerService;
    @Resource
    private ProjClientRoleService projClientRoleService;
    @Resource
    private ContractLeaseItemService contractLeaseItemService;
    @Resource
    private CommonProcessPrepareService commonProcessPrepareService;
    @Resource
    private Id2NameService id2NameService;

    @Override
    public List<ProcessModelTypeEnum> handleModelTypeList() {
        return ListUtil.toList(ProcessModelTypeEnum.ContractNormalSettleFlow, ProcessModelTypeEnum.ContractEarlySettleFlow);
    }

    @Override
    public ContractStatus getContractStatus(String modelKey, boolean processPass) {
        return processPass ? ContractStatus.SETTLE : null;
    }

    @Override
    public ContractProcessStatusEnum getContractProcessStatus(String modelKey, boolean processPass) {
        return processPass ? ContractProcessStatusEnum.SETTLE_PASS : ContractProcessStatusEnum.SETTLE_CANCEL;
    }

    @Override
    public void recordStatus(String modelKey, Long contractId, boolean processPass) {
        // 是否更新状态 还要看情况
        if (processPass) {
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
            // 提前生成现金流，不然等到判断现金流全部核销完后再生成逻辑就有问题了
            contractService.notifyOnSettlePass(contractBaseInfo, ProcessModelTypeEnum.getByName(modelKey));
            // 结清需要进一步校验合同下的所有现金流是否都已核销完毕
            boolean bjz = false;
            boolean cashFlow = false;
            // 校验保证金余额是否为0
            long earnestBalance = marginBaseInfoService.getMarginBalance(contractId);
            if (earnestBalance == 0) {
                bjz = true;
            } else {
                log.info("合同结清流程审批通过，但是保证金余额不等于0，不执行合同状态变更动作[contractId: {}]", contractId);
            }
            // 校验合同下的所有现金流是否都已核销完毕
            if (collectionBaseInfoService.allCashFlowIsVerified(contractId)) {
                cashFlow = true;
            } else {
                log.info("合同结清流程审批通过，但是存在未核销完毕的现金流，不执行合同状态变更动作[contractId: {}]", contractId);
            }
            //这里提前结清不再变更，改为待发起增加结清确认流程
            if (ProcessModelTypeEnum.ContractEarlySettleFlow.name().equals(modelKey)) {
                CommonProcessPrepare processPrepare = CommonProcessPrepare.builder()
                        .processType(ProcessModelTypeEnum.ContractEarlySettleConfirmFlow.name())
                        .businessId(String.valueOf(contractId))
                        .formName(String.join("-", contractBaseInfo.getContractCode(), ProcessModelTypeEnum.ContractEarlySettleConfirmFlow.getDisplay()))
                        .projName(contractBaseInfo.getProjName())
                        .clientName(id2NameService.clientId2NameSingle(contractBaseInfo.getClientId()))
                        .currentNode("项目主办待提交")
                        .currentAssignee(JSONUtil.toJsonStr(Collections.singletonList(contractBaseInfo.getProjSponsorUserId())))
                        .applyTime(LocalDateTime.now())
                        .status(CommonProcessPrepareStatus.PEND_COMMIT.name())
                        .build();
                commonProcessPrepareService.save(processPrepare);
                commonProcessPrepareService.noticeMessage(processPrepare);
            } else {
                if (bjz && cashFlow) {
                    super.recordStatus(modelKey, contractId, processPass);
                }
            }
            // 清除流程子类型数据
            contractBaseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.SETTLE_PASS.name(), null, contractId);

            projClientRoleService.contractSettle(contractId);
        } else {
            // 审批没通过 更新状态
            super.recordStatus(modelKey, contractId, processPass);
            //修改罚息状态
            List<CollectionBaseInfo> collectionList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .eq(CollectionBaseInfo::getContractId, contractId));
            if(ObjectUtil.isNotEmpty(collectionList)) {
                List<Long> collectionIds = collectionList.stream().map(CollectionBaseInfo::getId).collect(Collectors.toList());
                LambdaUpdateWrapper<CollectionBaseInfo> collectionBaseInfoLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                collectionBaseInfoLambdaUpdateWrapper.set(CollectionBaseInfo::getPenaltyInterestCalculateFlag, YesOrNoNumberEnum.NO.getCode());
                collectionBaseInfoLambdaUpdateWrapper.in(CollectionBaseInfo::getId, collectionIds);
                collectionBaseInfoService.update(null, collectionBaseInfoLambdaUpdateWrapper);
                // 重新计算
                SpringContextHolder.getBean(CollectionOverdueRecordInfoService.class).doRerunPenaltyInterest(collectionIds);
            }
        }
    }

    @Override
    public void customProcessPass(String modelKey, Long contractId, Integer endType, Long startUserId, String processInstanceId) {
        // 解绑租赁物
        contractLeaseItemService.unbindLeaseItem(contractId);
        // 通知苍穹结清退保证金
        // 这个方法体里面是空的了，还有必要调用吗？
        financialManagerService.earnestRecord(contractId);
    }

    @Override
    public void customProcessFail(String modelKey, Long contractId, Integer endType, Long startUserId, String processInstanceId) {
        // 删除结清方案
        // 结清方案增加版本后无需删除 since 2022-12-19
//        contractSettlePlanService.removeByContractId(contractId);

        // 通知苍穹结清退保证金
        financialManagerService.earnestRecord(contractId);
    }

    @Override
    public void afterAllHook(String modelKey, Long contractId, Integer endType, Long startUserId, String processInstanceId) {
        /*if (ProcessModelTypeEnum.ContractEarlySettleFlow.name().equals(modelKey) && ProcessBusinessStatusEnum.success(endType)) {
            // 提前结清 记录特殊交易
            contractService.recordSpecialTrade(contractId, ContractSpecialTradeTypeEnum.SETTLE_IN_ADVANCE);
        }*/
    }

    @Override
    public void notifyCollection(String modelKey, Long contractId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
//        contractService.notifyOnSettlePass(contractBaseInfo, ProcessModelTypeEnum.getByName(modelKey));
        //提前结清不再变更，只认为是待变更
        if (ProcessModelTypeEnum.ContractEarlySettleFlow.name().equals(modelKey)) {
            contractService.notifyOnEarlySettlePass(contractBaseInfo, ProcessModelTypeEnum.getByName(modelKey));
        } else {
            super.notifyCollection(modelKey, contractId);
        }
    }
}
