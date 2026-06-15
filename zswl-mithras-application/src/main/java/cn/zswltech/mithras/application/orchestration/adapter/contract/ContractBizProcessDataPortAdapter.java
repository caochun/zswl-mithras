package cn.zswltech.mithras.application.orchestration.adapter.contract;

import cn.zswltech.mithras.contract.application.process.prepare.ContractBizProcessDataPort;
import cn.zswltech.mithras.workflow.process.BizProcessDataService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ContractBizProcessDataPortAdapter implements ContractBizProcessDataPort {

    @Resource
    private BizProcessDataService bizProcessDataService;

    @Override
    public void recordBizData(String processInstanceId, Long clientId) {
        bizProcessDataService.recordBizData(processInstanceId, clientId);
    }
}
