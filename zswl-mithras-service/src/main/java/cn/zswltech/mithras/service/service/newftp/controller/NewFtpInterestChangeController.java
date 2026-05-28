package cn.zswltech.mithras.service.service.newftp.controller;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.newftp.NewFtpInterestChangeApi;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.newftp.NewFtpInterestChangeApplyRecordRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpInterestChangeApplySaveREQ;
import cn.zswltech.mithras.service.service.newftp.service.NewFtpChangeApplyRecordService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2025/3/4
 * @description
 */
@RestController
public class NewFtpInterestChangeController implements NewFtpInterestChangeApi {
    @Resource
    private NewFtpChangeApplyRecordService newFtpChangeApplyRecordService;

    @Override
    public R<NewFtpInterestChangeApplyRecordRSP> detail(SinglePkREQ req) {
        return R.ok(newFtpChangeApplyRecordService.detail(req.getId()));
    }

    @Override
    public R<Long> save(NewFtpInterestChangeApplySaveREQ req) {
        return R.ok(newFtpChangeApplyRecordService.save(req));
    }

    @Override
    public R<String> submit(SinglePkREQ req) {
        return R.ok(newFtpChangeApplyRecordService.submit(req.getId()));
    }
}
