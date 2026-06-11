package cn.zswltech.mithras.projectprocess.controller.projpricing;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.projpricing.ProjPricingButtonStatusRsp;
import cn.zswltech.mithras.dto.projpricing.ProjPricingCreateREQ;
import cn.zswltech.mithras.dto.projpricing.ProjPricingCreateRSP;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoAddByGroupCreditREQ;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoAddREQ;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoAddRSP;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoListREQ;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoListRSP;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoModifyREQ;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoRemoveREQ;
import cn.zswltech.mithras.api.projpricing.ProjPricingBaseInfoApi;
import cn.zswltech.mithras.projectprocess.application.projpricing.ProjPricingBaseInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class ProjPricingBaseInfoController implements ProjPricingBaseInfoApi {
    @Resource
    private ProjPricingBaseInfoApplicationService projPricingBaseInfoApplicationService;

    @Override
    public R<ProjPricingCreateRSP> create(ProjPricingCreateREQ req) {
        return projPricingBaseInfoApplicationService.create(req);
    }

    @Override
    public R<ProjPricingBaseInfoAddRSP> add(ProjPricingBaseInfoAddREQ req) {
        return projPricingBaseInfoApplicationService.add(req);
    }

    @Override
    public R<ProjPricingBaseInfoAddRSP> addByGroupCredit(ProjPricingBaseInfoAddByGroupCreditREQ req) {
        return projPricingBaseInfoApplicationService.addByGroupCredit(req);
    }

    @Override
    public R<Void> modify(ProjPricingBaseInfoModifyREQ req) {
        return projPricingBaseInfoApplicationService.modify(req);
    }

    @Override
    public R<PageR<ProjPricingBaseInfoListRSP>> list(ProjPricingBaseInfoListREQ req) {
        return projPricingBaseInfoApplicationService.list(req);
    }

    @Override
    public R<ProjPricingBaseInfoDetailRSP> detail(ProjPricingBaseInfoDetailREQ req) {
        return projPricingBaseInfoApplicationService.detail(req);
    }

    @Override
    public R<Void> disable(ProjPricingBaseInfoRemoveREQ req) {
        return projPricingBaseInfoApplicationService.disable(req);
    }

    @Override
    public R<ProjPricingButtonStatusRsp> buttonStatus(ProjPricingBaseInfoDetailREQ req) {
        return projPricingBaseInfoApplicationService.buttonStatus(req);
    }
}
