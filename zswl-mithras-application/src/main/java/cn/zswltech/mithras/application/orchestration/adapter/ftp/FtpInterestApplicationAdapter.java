package cn.zswltech.mithras.application.orchestration.adapter.ftp;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.ftp.FtpInterestBaseInfoRsp;
import cn.zswltech.mithras.dto.ftp.FtpInterestDetailReq;
import cn.zswltech.mithras.dto.ftp.FtpInterestDetailRsp;
import cn.zswltech.mithras.dto.ftp.FtpInterestIdReq;
import cn.zswltech.mithras.dto.ftp.FtpInterestPageListReq;
import cn.zswltech.mithras.dto.ftp.FtpInterestPageListRsp;
import cn.zswltech.mithras.dto.ftp.FtpInterestRecalculateReq;
import cn.zswltech.mithras.ftp.oldftp.service.application.FtpInterestApplicationService;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.ftp.FtpInterestBaseInfoService;
import cn.zswltech.mithras.application.orchestration.ftp.FtpInterestDetailRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;

@Service
public class FtpInterestApplicationAdapter implements FtpInterestApplicationService {

    @Resource
    private FtpInterestBaseInfoService ftpInterestBaseInfoService;
    @Resource
    private FtpInterestDetailRecordService ftpInterestDetailRecordService;

    @Override
    public PageR<FtpInterestPageListRsp> pageList(FtpInterestPageListReq req) {
        return ftpInterestBaseInfoService.pageList(req);
    }

    @Override
    public void recalculate(FtpInterestRecalculateReq req) {
        throw new MithrasException("暂不支持手动重算FTP计息");
    }

    @Override
    public LocalDate getLatestMonth() {
        return ftpInterestBaseInfoService.getLatestMonth();
    }

    @Override
    public FtpInterestBaseInfoRsp getBaseInfo(FtpInterestIdReq req) {
        return ftpInterestBaseInfoService.getBaseInfoRsp(req);
    }

    @Override
    public PageR<FtpInterestDetailRsp> listDetailRecordWithPage(FtpInterestDetailReq req) {
        return ftpInterestDetailRecordService.listDetailRecordWithPage(req);
    }
}
