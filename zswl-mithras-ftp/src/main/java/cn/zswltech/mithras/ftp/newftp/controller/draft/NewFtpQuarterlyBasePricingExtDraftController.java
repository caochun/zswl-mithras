package cn.zswltech.mithras.ftp.newftp.controller.draft;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.newftp.draft.NewFtpQuarterlyBasePricingExtDraftApi;
import cn.zswltech.mithras.dto.ftp.FtpGuidanceIdReq;
import cn.zswltech.mithras.dto.newftp.NewFtpQuarterlyBasePricingExtDraftDetailRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpQuarterlyBasePricingExtDraftModifyREQ;
import cn.zswltech.mithras.ftp.newftp.service.draft.NewFtpQuarterlyBasePricingExtDraftService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2025/7/16
 * @description
 */
@RestController
public class NewFtpQuarterlyBasePricingExtDraftController implements NewFtpQuarterlyBasePricingExtDraftApi {
    @Resource
    private NewFtpQuarterlyBasePricingExtDraftService newFtpQuarterlyBasePricingExtDraftService;

    @Override
    public R<Void> modify(NewFtpQuarterlyBasePricingExtDraftModifyREQ req) {
        newFtpQuarterlyBasePricingExtDraftService.modify(req);
        return R.ok();
    }

    @Override
    public R<NewFtpQuarterlyBasePricingExtDraftDetailRSP> detail(FtpGuidanceIdReq req) {
        return R.ok(newFtpQuarterlyBasePricingExtDraftService.detail(req.getId()));
    }
}
