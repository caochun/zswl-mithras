package cn.zswltech.mithras.ftp.oldftp.service.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.ftp.FtpGuidanceIdReq;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyGuidanceAddReq;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyGuidanceDetailRsp;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyGuidanceListReq;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyGuidanceListRsp;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyPricingRsp;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyValuationRsp;
import cn.zswltech.mithras.dto.ftp.FtpVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface FtpMonthlyGuidanceApplicationService {

    Long add(FtpMonthlyGuidanceAddReq req);

    PageR<FtpMonthlyGuidanceListRsp> list(FtpMonthlyGuidanceListReq req);

    FtpMonthlyGuidanceDetailRsp detail(FtpGuidanceIdReq req);

    List<FtpMonthlyValuationRsp> detailValuation(FtpGuidanceIdReq req);

    List<FtpMonthlyPricingRsp> detailPricing(FtpGuidanceIdReq req);

    String templateDownload();

    void importExcel(InputStream inputStream, Long guidanceId) throws Exception;

    FtpMonthlyGuidanceExportInfo getExportInfo(Long id);

    void exportExcel(Long guidanceId, OutputStream outputStream);

    void submit(Long id);

    PageR<CommonVersionListRSP> versionList(CommonVersionListREQ req);

    CommonVersionDiffRSP comparePreVersion(FtpVersionDiffREQ req);
}
