package cn.zswltech.mithras.service.controller.ftp;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.ftp.FtpInterestApi;
import cn.zswltech.mithras.dto.ftp.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.ftp.FtpInterestBaseInfoService;
import cn.zswltech.mithras.service.service.ftp.FtpInterestDetailRecordService;
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
    private FtpInterestBaseInfoService ftpInterestBaseInfoService;
    @Resource
    private FtpInterestDetailRecordService ftpInterestDetailRecordService;

    @Override
    public R<PageR<FtpInterestPageListRsp>> pageList(FtpInterestPageListReq req) {
        return R.ok(ftpInterestBaseInfoService.pageList(req));
    }

    @Override
    public R<Void> recalculate(FtpInterestRecalculateReq req) {
        throw new MithrasException("暂不支持手动重算FTP计息");
//        ftpInterestBaseInfoService.recalculate(req);
//        return R.ok();
    }

    @Override
    public R<LocalDate> getLatestMonth() {
        return R.ok(ftpInterestBaseInfoService.getLatestMonth());
    }

    @Override
    public R<FtpInterestBaseInfoRsp> getBaseInfo(@Valid FtpInterestIdReq req) {
        return R.ok(ftpInterestBaseInfoService.getBaseInfoRsp(req));
    }

    @Override
    public R<PageR<FtpInterestDetailRsp>> listDetailRecordWithPage(@Valid FtpInterestDetailReq req) {
        return R.ok(ftpInterestDetailRecordService.listDetailRecordWithPage(req));
    }
}
