package cn.zswltech.mithras.api.afterlease;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseReportListREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseReportUploadREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.projreview.report.ProjReviewReportDownloadREQ;
import cn.zswltech.mithras.dto.projreview.report.ProjReviewReportListRSP;
import cn.zswltech.mithras.dto.projreview.report.ProjReviewReportRemoveREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 *租后文件管理
 **/
@Api(tags = "租后调整-报告清单相关接口")
public interface AfterLeaseReportApi {

    @ApiOperation("获取报告清单列表")
    @PostMapping("/after/lease/report/list")
    R<List<Pair<String,List<ProjReviewReportListRSP>>>> list(@RequestBody @Valid AfterLeaseReportListREQ req);

    @ApiOperation("下载报告")
    @GetMapping("/after/lease/report/download")
    R<FileListRSP> download(@Valid ProjReviewReportDownloadREQ req);

    @ApiOperation("上传报告")
    @PostMapping("/after/lease/report/upload")
    R<Void> upload(@RequestParam("file") MultipartFile file, AfterLeaseReportUploadREQ req);

    @ApiOperation("删除报告")
    @PostMapping("/after/lease/report/remove")
    R<Void> remove(@RequestBody @Valid ProjReviewReportRemoveREQ projReviewReportRemoveREQ);

}
