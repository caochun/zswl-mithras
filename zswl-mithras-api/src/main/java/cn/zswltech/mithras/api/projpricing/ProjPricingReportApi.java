package cn.zswltech.mithras.api.projpricing;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.projpricing.report.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;


@Api(tags = "项目定价-报告清单相关接口")
public interface ProjPricingReportApi {
    @ApiOperation("获取报告清单列表")
    @PostMapping("/proj/pricing/report/list")
    R< List<Pair<String,List<ProjPricingReportListRSP>>>> list(@RequestBody @Valid ProjPricingReportListREQ ProjPricingReportListREQ);

    @ApiOperation("生成报告")
    @PostMapping("/proj/pricing/report/generate")
    R<Long> generate(@RequestBody @Valid ProjPricingReportGenerateREQ ProjPricingReportGenerateREQ) throws Exception;

    @ApiOperation("下载报告")
    @GetMapping("/proj/pricing/report/download")
    R<FileListRSP> download(@Valid ProjPricingReportDownloadREQ ProjPricingReportDownloadREQ) throws IOException;

    @ApiOperation("上传报告")
    @PostMapping("/proj/pricing/report/upload")
    R<Void> upload(@RequestParam("file") MultipartFile file, ProjPricingReportUploadREQ ProjPricingReportUploadREQ);

    @ApiOperation("删除报告")
    @PostMapping("/proj/pricing/report/remove")
    R<Void> remove(@RequestBody @Valid ProjPricingReportRemoveREQ ProjPricingReportRemoveREQ);

}
