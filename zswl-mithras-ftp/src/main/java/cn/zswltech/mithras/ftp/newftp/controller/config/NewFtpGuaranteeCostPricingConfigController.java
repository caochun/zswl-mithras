package cn.zswltech.mithras.ftp.newftp.controller.config;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.newftp.config.NewFtpGuaranteeCostPricingConfigApi;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.newftp.*;
import cn.zswltech.mithras.ftp.newftp.service.config.NewFtpGuaranteeCostPricingConfigService;
import cn.zswltech.mithras.ftp.newftp.service.draft.NewFtpGuaranteeCostPricingDraftService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author zhaozhengkang
 * @description 担保成本定价
 * @date 2023-05-21
 */
@RestController
public class NewFtpGuaranteeCostPricingConfigController implements NewFtpGuaranteeCostPricingConfigApi {

    @Resource
    private NewFtpGuaranteeCostPricingConfigService newFtpGuaranteeCostPricingConfigService;

    @Resource
    private NewFtpGuaranteeCostPricingDraftService newFtpGuaranteeCostPricingDraftService;


    @Override
    public R<Void> modify(NewFtpGuaranteeCostPricingModifyREQ req) {
        newFtpGuaranteeCostPricingConfigService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<NewFtpGuaranteeCostPricingListRSP>> guaranteeCostList(PageReq req) {
        return R.ok(newFtpGuaranteeCostPricingConfigService.list(req));
    }

    @Override
    public R<Void> flash(@Valid NewFtpGuaranteeCostPricingFlashREQ req) {
        newFtpGuaranteeCostPricingConfigService.flash(req.getMonth());
        return R.ok();
    }

}