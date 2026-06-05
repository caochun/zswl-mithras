package cn.zswltech.mithras.ftp.oldftp.service.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.ftp.FtpGuidanceIdReq;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyBasePricingRsp;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyCustomerPrincipalPricingRsp;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyEnterprisePricingRsp;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyGuidanceAddReq;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyGuidanceDetailRsp;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyGuidanceListReq;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyGuidanceListRsp;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyMonthPricingRsp;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyNewestRsp;
import cn.zswltech.mithras.dto.ftp.FtpVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface FtpQuarterlyGuidanceApplicationService {

    Long add(FtpQuarterlyGuidanceAddReq req);

    PageR<FtpQuarterlyGuidanceListRsp> list(FtpQuarterlyGuidanceListReq req);

    FtpQuarterlyGuidanceDetailRsp detail(FtpGuidanceIdReq req);

    List<FtpQuarterlyBasePricingRsp> detailPricingBase(FtpGuidanceIdReq req);

    List<FtpQuarterlyMonthPricingRsp> detailPricingMonth(FtpGuidanceIdReq req);

    List<FtpQuarterlyCustomerPrincipalPricingRsp> detailPricingCustomer(FtpGuidanceIdReq req);

    List<FtpQuarterlyEnterprisePricingRsp> detailPricingEnterprise(FtpGuidanceIdReq req);

    String templateDownload();

    void importExcel(InputStream inputStream, Long guidanceId) throws Exception;

    FtpQuarterlyGuidanceExportInfo getExportInfo(Long id);

    void exportExcel(Long guidanceId, OutputStream outputStream);

    void submit(Long id);

    PageR<CommonVersionListRSP> versionList(CommonVersionListREQ req);

    CommonVersionDiffRSP comparePreVersion(FtpVersionDiffREQ req);

    FtpQuarterlyNewestRsp newestVersionData(Long id);
}
