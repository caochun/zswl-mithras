package cn.zswltech.mithras.ftp.oldftp.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.ftp.FtpInterestApi;
import cn.zswltech.mithras.dto.ftp.*;
import cn.zswltech.mithras.ftp.oldftp.service.application.FtpInterestApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2023/5/17
 * @description
 */
@RestController
public class FtpInterestController implements FtpInterestApi {
    @Resource
    private FtpInterestApplicationService ftpInterestService;

    @Override
    public R<PageR<FtpInterestPageListRsp>> pageList(FtpInterestPageListReq req) {
        return R.ok(ftpInterestService.pageList(req));
    }

    @Override
    public R<Void> recalculate(FtpInterestRecalculateReq req) {
        ftpInterestService.recalculate(req);
        return R.ok();
    }

    @Override
    public R<LocalDate> getLatestMonth() {
        return R.ok(ftpInterestService.getLatestMonth());
    }

    @Override
    public R<FtpInterestBaseInfoRsp> getBaseInfo(@Valid FtpInterestIdReq req) {
        return R.ok(ftpInterestService.getBaseInfo(req));
    }

    @Override
    public R<PageR<FtpInterestDetailRsp>> listDetailRecordWithPage(@Valid FtpInterestDetailReq req) {
        return R.ok(ftpInterestService.listDetailRecordWithPage(req));
    }
}
