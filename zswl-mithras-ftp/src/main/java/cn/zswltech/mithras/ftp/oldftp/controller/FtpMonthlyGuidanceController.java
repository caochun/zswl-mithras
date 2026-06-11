package cn.zswltech.mithras.ftp.oldftp.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.ftp.FtpMonthlyGuidanceApi;
import cn.zswltech.mithras.dto.ftp.*;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.ftp.oldftp.service.application.FtpMonthlyGuidanceApplicationService;
import cn.zswltech.mithras.ftp.oldftp.service.application.FtpMonthlyGuidanceExportInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 月度指导
 * @date 2023-01-10
 */
@RestController
@Slf4j
public class FtpMonthlyGuidanceController implements FtpMonthlyGuidanceApi {

    @Resource
    private FtpMonthlyGuidanceApplicationService mainService;
    @Resource
    private HttpServletResponse response;

    @Override
    public R<Long> add(FtpMonthlyGuidanceAddReq req) {
        return R.ok(mainService.add(req));
    }

    @Override
    public R<PageR<FtpMonthlyGuidanceListRsp>> list(FtpMonthlyGuidanceListReq req) {
        PageR<FtpMonthlyGuidanceListRsp> rsps = mainService.list(req);
        return R.ok(rsps);
    }

    @Override
    public R<FtpMonthlyGuidanceDetailRsp> detail(FtpGuidanceIdReq req) {
        FtpMonthlyGuidanceDetailRsp rsp = mainService.detail(req);
        return R.ok(rsp);
    }

    @Override
    public R<List<FtpMonthlyValuationRsp>> detailValuation(FtpGuidanceIdReq req) {
        return R.ok(mainService.detailValuation(req));
    }

    @Override
    public R<List<FtpMonthlyPricingRsp>> detailPricing(FtpGuidanceIdReq req) {
        return R.ok(mainService.detailPricing(req));
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
            throw new MithrasException("导入excel文件出现错误!");
        }
        return R.ok();
    }

    @Override
    public void exportExcel(FtpGuidanceIdReq req) {
        try {
            FtpMonthlyGuidanceExportInfo exportInfo = mainService.getExportInfo(req.getId());
            String fileName = "FTP定价测算表" + exportInfo.getYear() + String.format("%02d", exportInfo.getMonth()) + ".xlsx";
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
            mainService.exportExcel(req.getId(), response.getOutputStream());
        } catch (Exception e) {
            throw new MithrasException("导出月度ftp定价指导出错!");
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
}
