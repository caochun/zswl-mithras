package cn.zswltech.mithras.application.orchestration.adapter.afterlease;

import cn.zswltech.mithras.afterlease.application.AfterLeaseSponsorAuthPort;
import cn.zswltech.mithras.application.orchestration.auth.rule.DataAuthSponsorUserRule;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AfterLeaseSponsorAuthPortAdapter implements AfterLeaseSponsorAuthPort {
    @Resource
    private DataAuthSponsorUserRule dataAuthSponsorUserRule;

    @Override
    public void check(String businessModule, Long businessId) {
        dataAuthSponsorUserRule.check(BusinessModuleEnum.valueOf(businessModule), businessId);
    }
}
