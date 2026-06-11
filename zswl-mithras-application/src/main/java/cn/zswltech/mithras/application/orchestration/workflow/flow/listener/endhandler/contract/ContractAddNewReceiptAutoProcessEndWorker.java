package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler.contract;

import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/12/13
 * @description
 */
@Component
public class ContractAddNewReceiptAutoProcessEndWorker extends AbstractContractProcessEndWorker {
    @Resource
    private ContractAddNewReceiptProcessEndWorker contractAddNewReceiptProcessEndWorker;

    @Override
    public List<ProcessModelTypeEnum> handleModelTypeList() {
        return Collections.singletonList(ProcessModelTypeEnum.ContractAddNewReceiptAutoFlow);
    }

    @Override
    public void customProcessPass(String modelKey, Long contractId, Integer endType, Long startUserId, String processInstanceId) {
        contractAddNewReceiptProcessEndWorker.customProcessPass(modelKey, contractId, endType, startUserId, processInstanceId);
    }

    @Override
    public ContractStatus getContractStatus(String modelKey, boolean processPass) {
        return contractAddNewReceiptProcessEndWorker.getContractStatus(modelKey, processPass);
    }

    @Override
    public ContractProcessStatusEnum getContractProcessStatus(String modelKey, boolean processPass) {
        return contractAddNewReceiptProcessEndWorker.getContractProcessStatus(modelKey, processPass);
    }
}
