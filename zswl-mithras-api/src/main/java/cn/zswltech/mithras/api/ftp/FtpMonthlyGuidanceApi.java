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
 * @description 月度指导
 * @date 2023-01-10
 */
@Api(tags = "月度指导-接口")
@RequestMapping("/ftp/monthly/guidance")
public interface FtpMonthlyGuidanceApi {

    @ApiOperation("新增月度指导")
    @PostMapping("/add")
    R<Long> add(@RequestBody @Valid FtpMonthlyGuidanceAddReq req);

    @ApiOperation("月度指导列表")
    @PostMapping("/list")
    R<PageR<FtpMonthlyGuidanceListRsp>> list(@RequestBody @Valid FtpMonthlyGuidanceListReq req);

    @ApiOperation("月度指导详情")
    @PostMapping("/detail")
    R<FtpMonthlyGuidanceDetailRsp> detail(@RequestBody @Valid FtpGuidanceIdReq req);

    @ApiOperation("月度指导详情-计价列表")
    @PostMapping("/detail/valuation")
    R<List<FtpMonthlyValuationRsp>> detailValuation(@RequestBody @Valid FtpGuidanceIdReq req);

    @ApiOperation("月度指导详情-定价指导列表")
    @PostMapping("/detail/pricing")
    R<List<FtpMonthlyPricingRsp>> detailPricing(@RequestBody @Valid FtpGuidanceIdReq req);

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

}