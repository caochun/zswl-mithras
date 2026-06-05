package cn.zswltech.mithras.projectprocess.controller.projestablish;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailREQ;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailRSP;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceModifyREQ;
import javax.validation.Valid;
import cn.zswltech.mithras.api.projestablish.ProjEstablishPriceApi;
import cn.zswltech.mithras.projectprocess.application.projestablish.ProjEstablishPriceApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class ProjEstablishPriceController implements ProjEstablishPriceApi {
    @Resource
    private ProjEstablishPriceApplicationService projEstablishPriceApplicationService;

    @Override
    public R<Void> modify(ProjEstablishPriceModifyREQ req) {
        return projEstablishPriceApplicationService.modify(req);
    }

    @Override
    public R<ProjEstablishPriceDetailRSP> detail(ProjEstablishPriceDetailREQ req) {
        return projEstablishPriceApplicationService.detail(req);
    }
}
