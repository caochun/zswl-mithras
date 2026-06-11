package cn.zswltech.mithras.application.orchestration.adapter.projectprocess;

import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.projectprocess.application.projpricing.ProjPricingUpdateSupport;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingService;
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
