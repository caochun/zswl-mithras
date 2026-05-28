package cn.zswltech.mithras.api.projreview;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.projreview.report.*;
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

/**
 * @author dingqi
 * @date 2022/8/1
 * @description
 */
@Api(tags = "项目评审-报告清单相关接口")
public interface ProjReviewReportApi {
    @ApiOperation("获取报告清单列表")
    @PostMapping("/proj/review/report/list")
    R< List<Pair<String,List<ProjReviewReportListRSP>>>> list(@RequestBody @Valid ProjReviewReportListREQ projReviewReportListREQ);

    @ApiOperation("生成报告")
    @PostMapping("/proj/review/report/generate")
    R<Long> generate(@RequestBody @Valid ProjReviewReportGenerateREQ projReviewReportGenerateREQ) throws Exception;

    @ApiOperation("下载报告")
    @GetMapping("/proj/review/report/download")
    R<FileListRSP> download(@Valid ProjReviewReportDownloadREQ projReviewReportDownloadREQ) throws IOException;

    @ApiOperation("上传报告")
    @PostMapping("/proj/review/report/upload")
    R<Void> upload(@RequestParam("file") MultipartFile file, ProjReviewReportUploadREQ projReviewReportUploadREQ);

    @ApiOperation("删除报告")
    @PostMapping("/proj/review/report/remove")
    R<Void> remove(@RequestBody @Valid ProjReviewReportRemoveREQ projReviewReportRemoveREQ);

    @ApiOperation("生成项目收益率报告")
    @PostMapping("/proj/review/report/generate/earningsRate")
    R<Long> generateEarningsRate(@RequestBody @Valid ProjReviewReportGenerateREQ projReviewReportGenerateREQ) ;
}
