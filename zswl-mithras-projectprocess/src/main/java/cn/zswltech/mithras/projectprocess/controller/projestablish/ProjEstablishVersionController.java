package cn.zswltech.mithras.projectprocess.controller.projestablish;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishEffectREQ;
import cn.zswltech.mithras.dto.projestablish.version.ProjEstablishVersionDetailREQ;
import cn.zswltech.mithras.dto.projestablish.version.ProjEstablishVersionDetailRSP;
import cn.zswltech.mithras.dto.projestablish.version.ProjEstablishVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import javax.validation.Valid;
import java.util.Map;
import cn.zswltech.mithras.api.projestablish.ProjEstablishVersionApi;
import cn.zswltech.mithras.projectprocess.application.projestablish.ProjEstablishVersionApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class ProjEstablishVersionController implements ProjEstablishVersionApi {
    @Resource
    private ProjEstablishVersionApplicationService projEstablishVersionApplicationService;

    @Override
    public R<Void> effect(ProjEstablishEffectREQ req) {
        return projEstablishVersionApplicationService.effect(req);
    }

    @Override
    public R<PageR<CommonVersionListRSP>> list(CommonVersionListREQ req) {
        return projEstablishVersionApplicationService.list(req);
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(ProjEstablishVersionDiffREQ req) {
        return projEstablishVersionApplicationService.comparePreVersion(req);
    }

    @Override
    public R<ProjEstablishVersionDetailRSP> versionDetail(ProjEstablishVersionDetailREQ req) {
        return projEstablishVersionApplicationService.versionDetail(req);
    }
}
