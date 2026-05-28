package cn.zswltech.mithras.service.flow.listener.endhandler.contract;

import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/12/13
 * @description
 */
@Slf4j
@Component
public class ContractStartRentAutoProcessEndWorker extends AbstractContractProcessEndWorker {
    @Resource
    private ContractStartRentProcessEndWorker contractStartRentProcessEndWorker;

    @Override
    public List<ProcessModelTypeEnum> handleModelTypeList() {
        return Collections.singletonList(ProcessModelTypeEnum.ContractStartRentAutoFlow);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void customProcessPass(String modelKey, Long contractId, Integer endType, Long startUserId, String processInstanceId) {
        contractStartRentProcessEndWorker.customProcessPass(modelKey, contractId, endType, startUserId, processInstanceId);
    }

    /**
     * 通知收款
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void notifyCollection(String modelKey, Long contractId) {
        contractStartRentProcessEndWorker.notifyCollection(modelKey, contractId);
    }

    @Override
    public ContractStatus getContractStatus(String modelKey, boolean processPass) {
        return contractStartRentProcessEndWorker.getContractStatus(modelKey, processPass);
    }

    @Override
    public ContractProcessStatusEnum getContractProcessStatus(String modelKey, boolean processPass) {
        return contractStartRentProcessEndWorker.getContractProcessStatus(modelKey, processPass);
    }
}
