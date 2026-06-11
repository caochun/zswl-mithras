package cn.zswltech.mithras.ftp.newftp.controller.draft;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.newftp.draft.NewFtpGuaranteeCostPricingDraftApi;
import cn.zswltech.mithras.dto.newftp.NewFtpCommonDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpGuaranteeCostPricingListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpGuaranteeCostPricingModifyREQ;
import cn.zswltech.mithras.ftp.newftp.service.draft.NewFtpGuaranteeCostPricingDraftService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author yangxiong
 * @date 2024/3/29/16:25
 * @description
 */
@RestController
public class NewFtpGuaranteeCostPricingDraftController implements NewFtpGuaranteeCostPricingDraftApi {

    @Resource
    private NewFtpGuaranteeCostPricingDraftService newFtpGuaranteeCostPricingDraftService;

    @Override
    public R<Void> modifyDraft(@Valid NewFtpGuaranteeCostPricingModifyREQ req) {
        newFtpGuaranteeCostPricingDraftService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<NewFtpGuaranteeCostPricingListRSP>> listDraft(@Valid NewFtpCommonDetailReq req) {
        return R.ok(newFtpGuaranteeCostPricingDraftService.list(req));
    }

    @Override
    public R<Void> flashDraft(@Valid NewFtpDetailReq req) {
        newFtpGuaranteeCostPricingDraftService.add(req.getMainId());
        return R.ok();
    }

}
