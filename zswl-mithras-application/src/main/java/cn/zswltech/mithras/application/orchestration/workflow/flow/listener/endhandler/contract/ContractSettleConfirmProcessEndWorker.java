package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler.contract;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractSpecialTradeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.application.orchestration.client.ProjClientRoleService;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.contract.application.ContractLeaseItemService;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 合同结清
 *
 * @author wangchuanhao
 * @date 2022/12/15 10:50 AM
 */
@Component
@Slf4j
public class ContractSettleConfirmProcessEndWorker extends AbstractContractProcessEndWorker {

    @Resource
    private ProjClientRoleService projClientRoleService;
    @Resource
    private ContractLeaseItemService contractLeaseItemService;

    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private MarginBaseInfoService marginBaseInfoService;

    @Override
    public List<ProcessModelTypeEnum> handleModelTypeList() {
        return ListUtil.toList(ProcessModelTypeEnum.ContractEarlySettleConfirmFlow);
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
        // 这里直接结清了
        if (processPass) {
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
            if (bjz && cashFlow) {
                super.recordStatus(modelKey, contractId, processPass);
            }
            // 清除流程子类型数据
            projClientRoleService.contractSettle(contractId);
        } else {
            // 审批没通过 更新状态
            super.recordStatus(modelKey, contractId, processPass);
        }
    }

    @Override
    public void afterAllHook(String modelKey, Long contractId, Integer endType, Long startUserId, String processInstanceId) {
        if (ProcessModelTypeEnum.ContractEarlySettleConfirmFlow.name().equals(modelKey) && ProcessBusinessStatusEnum.success(endType)) {
            // 提前结清 记录特殊交易 报送
            contractService.recordSpecialTrade(contractId, ContractSpecialTradeTypeEnum.SETTLE_IN_ADVANCE, processInstanceId);
        }
    }

    @Override
    public void customProcessPass(String modelKey, Long contractId, Integer endType, Long startUserId, String processInstanceId) {
        // 解绑租赁物
        contractLeaseItemService.unbindLeaseItem(contractId);
        // 异步执行收入确认分摊逻辑
        this.doIncomeSharing(modelKey, contractId);
    }

    @Override
    public void notifyCollection(String modelKey, Long contractId) {
        super.notifyCollection(modelKey, contractId);
    }
}