package cn.zswltech.mithras.ftp.newftp.controller.draft;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.newftp.draft.NewFtpMonthlyGuidanceExtDraftApi;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyGuidanceExtDraftDetailRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyGuidanceExtDraftModifyREQ;
import cn.zswltech.mithras.ftp.newftp.service.draft.NewFtpMonthlyGuidanceExtDraftService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author yangxiong
 * @date 2024/3/29/16:13
 * @description
 */
@RestController
public class NewFtpMonthlyGuidanceExtDraftController implements NewFtpMonthlyGuidanceExtDraftApi {

    @Resource
    private NewFtpMonthlyGuidanceExtDraftService newFtpMonthlyGuidanceExtService;

    @Override
    public R<Void> modify(NewFtpMonthlyGuidanceExtDraftModifyREQ req) {
        newFtpMonthlyGuidanceExtService.modify(req);
        return R.ok();
    }

    @Override
    public R<NewFtpMonthlyGuidanceExtDraftDetailRSP> detail(NewFtpDetailReq req) {
        return R.ok(newFtpMonthlyGuidanceExtService.detail(req.getMainId()));
    }
}
