package cn.zswltech.mithras.service.service.newftp.controller.draft;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.newftp.draft.NewFtpFinancingCostPricingDraftApi;
import cn.zswltech.mithras.dto.newftp.NewFtpCommonDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpFinancingCostPricingFlashREQ;
import cn.zswltech.mithras.dto.newftp.NewFtpFinancingCostPricingListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpFinancingCostPricingModifyREQ;
import cn.zswltech.mithras.service.service.newftp.service.drift.NewFtpFinancingCostPricingDraftService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author yangxiong
 * @date 2024/3/29/16:30
 * @description
 */
@RestController
public class NewFtpFinancingCostPricingDraftController implements NewFtpFinancingCostPricingDraftApi {

    @Resource
    private NewFtpFinancingCostPricingDraftService financingCostPricingDraftService;

    @Override
    public R<PageR<NewFtpFinancingCostPricingListRSP>> draftList(NewFtpCommonDetailReq req) {
        return R.ok(financingCostPricingDraftService.list(req));
    }

    @Override
    public R<Void> flashDraft(@Valid NewFtpFinancingCostPricingFlashREQ req) {
        financingCostPricingDraftService.add(req.getMainId());
        return R.ok();
    }

    @Override
    public R<Void> modify(NewFtpFinancingCostPricingModifyREQ req) {
        return financingCostPricingDraftService.modify(req);
    }
}
