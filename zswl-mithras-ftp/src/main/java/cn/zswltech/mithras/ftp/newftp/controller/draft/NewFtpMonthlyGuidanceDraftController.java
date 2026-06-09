package cn.zswltech.mithras.ftp.newftp.controller.draft;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.newftp.draft.NewFtpMonthlyGuidanceDraftApi;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyGuidanceDetaiRsp;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyGuidanceModifyREQ;
import cn.zswltech.mithras.ftp.newftp.service.drift.NewFtpMonthlyGuidanceDraftService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author yangxiong
 * @date 2024/3/29/16:12
 * @description
 */
@RestController
public class NewFtpMonthlyGuidanceDraftController implements NewFtpMonthlyGuidanceDraftApi {

    @Resource
    private NewFtpMonthlyGuidanceDraftService newFtpMonthlyGuidanceService;

    @Override
    public R<Void> modify(NewFtpMonthlyGuidanceModifyREQ req) {
        newFtpMonthlyGuidanceService.modify(req);
        return R.ok();
    }

    @Override
    public R<NewFtpMonthlyGuidanceDetaiRsp> detail(NewFtpDetailReq req) {
        return R.ok(newFtpMonthlyGuidanceService.detail(req.getMainId()));
    }

    @Override
    public R<Void> monthlyGuidanceAdd(NewFtpDetailReq req) {
        newFtpMonthlyGuidanceService.monthlyGuidanceAdd(req.getMainId());
        return R.ok();
    }
}
