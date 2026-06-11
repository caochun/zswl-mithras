package cn.zswltech.mithras.application.orchestration.adapter.projectprocess;

import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.projectprocess.application.projestablish.ProjEstablishUpdateSupport;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ProjEstablishUpdateSupportAdapter implements ProjEstablishUpdateSupport {

    @Resource
    private ProjEstablishService projEstablishService;

    @Override
    public boolean canSave(Long projEstablishId) {
        return projEstablishService.canSave(projEstablishId);
    }

    @Override
    public boolean hasRelatedProcess(Long projEstablishId) {
        return projEstablishService.findRelatedProcess(projEstablishId) != null;
    }
}
