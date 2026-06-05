package cn.zswltech.mithras.ftp.oldftp.service.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.ftp.FtpInterestBaseInfoRsp;
import cn.zswltech.mithras.dto.ftp.FtpInterestDetailReq;
import cn.zswltech.mithras.dto.ftp.FtpInterestDetailRsp;
import cn.zswltech.mithras.dto.ftp.FtpInterestIdReq;
import cn.zswltech.mithras.dto.ftp.FtpInterestPageListReq;
import cn.zswltech.mithras.dto.ftp.FtpInterestPageListRsp;
import cn.zswltech.mithras.dto.ftp.FtpInterestRecalculateReq;

import java.time.LocalDate;

public interface FtpInterestApplicationService {

    PageR<FtpInterestPageListRsp> pageList(FtpInterestPageListReq req);

    void recalculate(FtpInterestRecalculateReq req);

    LocalDate getLatestMonth();

    FtpInterestBaseInfoRsp getBaseInfo(FtpInterestIdReq req);

    PageR<FtpInterestDetailRsp> listDetailRecordWithPage(FtpInterestDetailReq req);
}
