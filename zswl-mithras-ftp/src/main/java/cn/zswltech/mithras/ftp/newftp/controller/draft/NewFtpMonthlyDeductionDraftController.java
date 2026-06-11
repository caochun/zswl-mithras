package cn.zswltech.mithras.ftp.newftp.controller.draft;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.newftp.draft.NewFtpMonthlyDeductionDraftApi;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyDeductionListREQ;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyDeductionListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyDeductionModifyREQ;
import cn.zswltech.mithras.ftp.newftp.service.draft.NewFtpMonthlyDeductionDraftService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/3/29/16:11
 * @description
 */
@RestController
public class NewFtpMonthlyDeductionDraftController implements NewFtpMonthlyDeductionDraftApi {

    @Resource
    private NewFtpMonthlyDeductionDraftService baseService;
    @Resource
    private NewFtpMonthlyDeductionDraftService monthlyDeductionDraftService;

    @Override
    public R<Void> modify(NewFtpMonthlyDeductionModifyREQ req) {
        baseService.modify(req);
        return R.ok();
    }

    @Override
    public R<List<NewFtpMonthlyDeductionListRSP>> list(NewFtpMonthlyDeductionListREQ req) {
        return R.ok(baseService.list(req));
    }

    @Override
    public R<Void> refresh(NewFtpMonthlyDeductionListREQ req) {
        baseService.refresh(req.getMainId());
        return R.ok();
    }

    @Override
    public R<Void> addMonthlyDeduction(NewFtpMonthlyDeductionListREQ req) {
        return monthlyDeductionDraftService.addMonthlyDeduction(req.getMainId());
    }
}
