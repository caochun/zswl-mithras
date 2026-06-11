package cn.zswltech.mithras.ftp.oldftp.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.ftp.FtpQuarterlyGuidanceApi;
import cn.zswltech.mithras.dto.ftp.*;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.ftp.oldftp.service.application.FtpQuarterlyGuidanceApplicationService;
import cn.zswltech.mithras.ftp.oldftp.service.application.FtpQuarterlyGuidanceExportInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 季度最低收益率指导
 * @date 2023-01-09
 */
@RestController
@Slf4j
public class FtpQuarterlyGuidanceController implements FtpQuarterlyGuidanceApi {

    @Resource
    private FtpQuarterlyGuidanceApplicationService mainService;
    @Resource
    private HttpServletResponse response;

    @Override
    public R<Long> add(FtpQuarterlyGuidanceAddReq req) {
        return R.ok(mainService.add(req));
    }

    @Override
    public R<PageR<FtpQuarterlyGuidanceListRsp>> list(FtpQuarterlyGuidanceListReq req) {
        PageR<FtpQuarterlyGuidanceListRsp> rsps = mainService.list(req);
        return R.ok(rsps);
    }

    @Override
    public R<FtpQuarterlyGuidanceDetailRsp> detail(FtpGuidanceIdReq req) {
        return R.ok(mainService.detail(req));
    }

    @Override
    public R<List<FtpQuarterlyBasePricingRsp>> detailPricingBase(FtpGuidanceIdReq req) {
        return R.ok(mainService.detailPricingBase(req));
    }

    @Override
    public R<List<FtpQuarterlyMonthPricingRsp>> detailPricingMonth(FtpGuidanceIdReq req) {
        return R.ok(mainService.detailPricingMonth(req));
    }

    @Override
    public R<List<FtpQuarterlyCustomerPrincipalPricingRsp>> detailPricingCustomer(FtpGuidanceIdReq req) {
        return R.ok(mainService.detailPricingCustomer(req));
    }

    @Override
    public R<List<FtpQuarterlyEnterprisePricingRsp>> detailPricingEnterprise(FtpGuidanceIdReq req) {
        return R.ok(mainService.detailPricingEnterprise(req));
    }

    @Override
    public R<String> templateDownload() {
        return R.ok(mainService.templateDownload());
    }

    @Override
    public R<Void> importExcel(MultipartFile file, Long guidanceId) {
        try {
            mainService.importExcel(file.getInputStream(), guidanceId);
        } catch (Exception e) {
            throw new MithrasException("导入文件发生错误!");
        }
        return R.ok();
    }

    @Override
    public void exportExcel(FtpGuidanceIdReq req) {
        try {
            FtpQuarterlyGuidanceExportInfo exportInfo = mainService.getExportInfo(req.getId());
            String fileName = "最低收益率指导" + exportInfo.getYear() + "第" + exportInfo.getQuarter() + "季度.xlsx";
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(URLEncoder.encode(fileName, "utf-8").getBytes("UTF-8"), "ISO-8859-1"));
            mainService.exportExcel(req.getId(), response.getOutputStream());
        } catch (Exception e) {
            throw new MithrasException("导出季度最低收益率指导出错!");
        }
    }

    @Override
    public R<Void> submit(FtpGuidanceIdReq req) {
        mainService.submit(req.getId());
        return R.ok();
    }

    @Override
    public R<PageR<CommonVersionListRSP>> list(CommonVersionListREQ req) {
        return R.ok(mainService.versionList(req));
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(FtpVersionDiffREQ req) {
        return R.ok(mainService.comparePreVersion(req));
    }

    @Override
    public R<FtpQuarterlyNewestRsp> newestVersionData(FtpGuidanceIdReq req) {
        return R.ok(mainService.newestVersionData(req.getId()));
    }

}
