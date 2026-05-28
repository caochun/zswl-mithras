package cn.zswltech.mithras.api.ftp;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.ftp.*;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 季度指导
 * @date 2023-01-09
 */
@Api(tags = "季度指导-接口")
@RequestMapping("/ftp/quarterly/guidance")
public interface FtpQuarterlyGuidanceApi {

    @ApiOperation("新增季度指导列表")
    @PostMapping("/add")
    R<Long> add(@RequestBody @Valid FtpQuarterlyGuidanceAddReq req);

    @ApiOperation("季度指导列表")
    @PostMapping("/list")
    R<PageR<FtpQuarterlyGuidanceListRsp>> list(@RequestBody @Valid FtpQuarterlyGuidanceListReq req);
    @ApiOperation("季度指导详情")
    @PostMapping("/detail")
    R<FtpQuarterlyGuidanceDetailRsp> detail(@RequestBody @Valid FtpGuidanceIdReq req);

    @ApiOperation("季度指导详情-sheet1列表")
    @PostMapping("/detail/pricing/base")
    R<List<FtpQuarterlyBasePricingRsp>> detailPricingBase(@RequestBody @Valid FtpGuidanceIdReq req);

    @ApiOperation("季度指导详情-sheet2-表1-左")
    @PostMapping("/detail/pricing/month")
    R<List<FtpQuarterlyMonthPricingRsp>> detailPricingMonth(@RequestBody @Valid FtpGuidanceIdReq req);

    @ApiOperation("季度指导详情-sheet2-表1-右")
    @PostMapping("/detail/pricing/cuntomer")
    R<List<FtpQuarterlyCustomerPrincipalPricingRsp>> detailPricingCustomer(@RequestBody @Valid FtpGuidanceIdReq req);

    @ApiOperation("季度指导详情-sheet2-表2")
    @PostMapping("/detail/pricing/enterprise")
    R<List<FtpQuarterlyEnterprisePricingRsp>> detailPricingEnterprise(@RequestBody @Valid FtpGuidanceIdReq req);

    @ApiOperation("季度指导模板下载")
    @GetMapping("/template/download")
    R<String> templateDownload();

    @ApiOperation("excel导入")
    @PostMapping("/excel/import")
    R<Void> importExcel(@RequestParam("file") MultipartFile file, @RequestParam("guidanceId") Long guidanceId);

    @ApiOperation("excel导出")
    @PostMapping("/excel/export")
    void exportExcel(@RequestBody @Valid FtpGuidanceIdReq req);

    @ApiOperation("提交审批")
    @PostMapping("/submit")
    R<Void> submit(@RequestBody @Valid FtpGuidanceIdReq req);

    @ApiOperation("版本表列")
    @PostMapping("/version/list")
    R<PageR<CommonVersionListRSP>> list(@RequestBody @Valid CommonVersionListREQ req);

    @ApiOperation("版本比较详情（与上一版本比较）")
    @PostMapping("/compare/preVersion")
    R<CommonVersionDiffRSP> comparePreVersion(@RequestBody @Valid FtpVersionDiffREQ req);
    @ApiOperation("变更前数据")
    @PostMapping("/newest")
    public R<FtpQuarterlyNewestRsp> newestVersionData(@RequestBody @Valid FtpGuidanceIdReq req);
}