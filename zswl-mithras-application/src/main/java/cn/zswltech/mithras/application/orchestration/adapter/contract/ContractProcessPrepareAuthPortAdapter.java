package cn.zswltech.mithras.application.orchestration.adapter.contract;

import cn.zswltech.mithras.contract.application.process.prepare.ContractProcessPrepareAuthPort;
import cn.zswltech.mithras.application.orchestration.auth.rule.DataAuthProcessRule;
import cn.zswltech.mithras.application.orchestration.auth.rule.DataAuthSponsorUserRule;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ContractProcessPrepareAuthPortAdapter implements ContractProcessPrepareAuthPort {

    @Resource
    private DataAuthSponsorUserRule dataAuthSponsorUserRule;
    @Resource
    private DataAuthProcessRule dataAuthProcessRule;

    @Override
    public void checkContractAuth(Long contractId) {
        dataAuthSponsorUserRule.check(BusinessModuleEnum.CONTRACT, contractId);
        dataAuthProcessRule.check(BusinessModuleEnum.CONTRACT, contractId);
    }
}
