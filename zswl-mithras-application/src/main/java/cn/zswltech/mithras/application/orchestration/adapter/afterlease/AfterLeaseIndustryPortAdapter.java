package cn.zswltech.mithras.application.orchestration.adapter.afterlease;

import cn.zswltech.mithras.afterlease.application.AfterLeaseIndustryPort;
import cn.zswltech.mithras.contract.gendoc.BusinessDataRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AfterLeaseIndustryPortAdapter implements AfterLeaseIndustryPort {
    @Resource
    private BusinessDataRepository businessDataRepository;

    @Override
    public String getIndustryTypeNameFromLocalCache(String code) {
        return businessDataRepository.getIndustryTypeNameFromLocalCache(code);
    }
}
