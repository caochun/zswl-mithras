package cn.zswltech.mithras.application.adapter.projectprocess;

import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.projectprocess.service.projpricing.ProjPricingUpdateSupport;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ProjPricingUpdateSupportAdapter implements ProjPricingUpdateSupport {

    @Resource
    private ProjPricingService projPricingService;

    @Override
    public boolean canSave(Long projectId) {
        return projPricingService.canSave(projectId);
    }

    @Override
    public boolean hasRelatedProcess(Long projectId) {
        return projPricingService.findRelatedProcess(projectId) != null;
    }
}
