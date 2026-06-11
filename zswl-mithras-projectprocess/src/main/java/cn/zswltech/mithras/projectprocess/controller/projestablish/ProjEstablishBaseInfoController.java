package cn.zswltech.mithras.projectprocess.controller.projestablish;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ClientAddressRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ClientIdREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ClientStockRiskExposureRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoAddREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoAddRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoModifyREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoRemoveREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoUpdateRatingREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoUpdateRatingRSP;
import cn.zswltech.mithras.api.projestablish.ProjEstablishBaseInfoApi;
import cn.zswltech.mithras.projectprocess.application.projestablish.ProjEstablishBaseInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class ProjEstablishBaseInfoController implements ProjEstablishBaseInfoApi {
    @Resource
    private ProjEstablishBaseInfoApplicationService projEstablishBaseInfoApplicationService;

    @Override
    public R<ProjEstablishBaseInfoAddRSP> add(ProjEstablishBaseInfoAddREQ req) {
        return projEstablishBaseInfoApplicationService.add(req);
    }

    @Override
    public R<ProjEstablishBaseInfoUpdateRatingRSP> updateRating(ProjEstablishBaseInfoUpdateRatingREQ req) {
        return projEstablishBaseInfoApplicationService.updateRating(req);
    }

    @Override
    public R<Void> modify(ProjEstablishBaseInfoModifyREQ req) {
        return projEstablishBaseInfoApplicationService.modify(req);
    }

    @Override
    public R<PageR<ProjEstablishBaseInfoListRSP>> list(ProjEstablishBaseInfoListREQ req) {
        return projEstablishBaseInfoApplicationService.list(req);
    }

    @Override
    public R<ProjEstablishBaseInfoListRSP> detail(ProjEstablishBaseInfoDetailREQ req) {
        return projEstablishBaseInfoApplicationService.detail(req);
    }

    @Override
    public R<Void> disable(ProjEstablishBaseInfoRemoveREQ req) {
        return projEstablishBaseInfoApplicationService.disable(req);
    }

    @Override
    public R<ClientStockRiskExposureRSP> getClientStockRiskExposure(ClientIdREQ req) {
        return projEstablishBaseInfoApplicationService.getClientStockRiskExposure(req);
    }

    @Override
    public R<ClientAddressRSP> getClientAddress(ClientIdREQ req) {
        return projEstablishBaseInfoApplicationService.getClientAddress(req);
    }
}
