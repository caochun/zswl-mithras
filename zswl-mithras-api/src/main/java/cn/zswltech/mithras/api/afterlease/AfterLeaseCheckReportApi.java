package cn.zswltech.mithras.api.afterlease;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.MultiplePkREQ;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.afterlease.*;
import cn.zswltech.mithras.dto.projreview.report.ProjReviewReportListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/16
 * @description
 */
@Api(tags = "租后检查项目（更改为客户）报告相关接口")
public interface AfterLeaseCheckReportApi {
    @ApiOperation("保存项目（更改为客户）检查报告基本信息")
    @PostMapping("/afterlease/check/project/report/base/save")
    R<Long> saveReportBase(@RequestBody @Valid AfterLeaseCheckReportBaseREQ req);

    @ApiOperation("获取项目（更改为客户）检查报告基本信息")
    @PostMapping("/afterlease/check/project/report/base/get")
    R<AfterLeaseCheckReportBaseRSP> getReportBase(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("保存项目（更改为客户）检查报告检查内容")
    @PostMapping("/afterlease/check/project/report/content/save")
    R<Void> saveReportContent(@RequestBody @Valid AfterLeaseCheckReportCSREQ req);

    @ApiOperation("获取项目（更改为客户）检查报告检查内容")
    @PostMapping("/afterlease/check/project/report/content/get")
    R<AfterLeaseCheckReportCSRSP> getReportContent(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("保存项目（更改为客户）检查报告检查总结")
    @PostMapping("/afterlease/check/project/report/summary/save")
    R<Void> saveReportSummary(@RequestBody @Valid AfterLeaseCheckReportCSREQ req);

    @ApiOperation("获取项目（更改为客户）检查报告检查总结")
    @PostMapping("/afterlease/check/project/report/summary/get")
    R<AfterLeaseCheckReportCSRSP> getReportSummary(@RequestBody @Valid SinglePkREQ req);

    @Deprecated
    @ApiOperation("获取非公用事业补充说明信息")
    @PostMapping("/afterlease/check/project/report/nonpublic/extra/get")
    R<List<AfterLeaseCheckReportNonPublicExtraRSP>> getNonPublicExtra(@RequestBody @Valid SinglePkREQ req);

    @Deprecated
    @ApiOperation("保存非公用事业补充说明信息")
    @PostMapping("/afterlease/check/project/report/nonpublic/extra/save")
    R<Void> saveNonPublicExtra(@RequestBody @Valid AfterLeaseCheckReportNonPublicExtraREQ req);

    @ApiOperation("上传非公用事业类型报告的检查附件")
    @PostMapping("/afterlease/check/project/report/nonpublic/file/upload")
    R<Void> uploadNonPublicReportFile(@Valid AfterLeaseCheckReportFileREQ req);

    @ApiOperation("获取非公用事业类型报告的检查附件列表")
    @PostMapping("/afterlease/check/project/report/nonpublic/file/list")
    R<List<Pair<String,List<ProjReviewReportListRSP>>>> listNonPublicReportFile(@RequestBody @Valid SinglePkREQ singlePkREQ);

    @ApiOperation("下载指定的单个项目（更改为客户）报告")
    @GetMapping("/afterlease/check/project/report/single/download")
    void downloadSingleReport(@Valid SinglePkREQ req);

    @ApiOperation("下载指定的批量项目（更改为客户）报告")
    @GetMapping("/afterlease/check/project/report/batch/download")
    void downloadBatchReport(@Valid MultiplePkREQ req);

    @ApiOperation("下载指定部门的项目（更改为客户）报告")
    @GetMapping("/afterlease/check/project/report/dept/download")
    void downloadDeptReport(@Valid AfterLeaseCheckReportDownloadREQ req);

    @ApiOperation("获取客户财务报表快照数据")
    @PostMapping("/afterlease/check/project/report/finance/snapshot")
    R<AfterLeaseCheckReportFinanceRSP> getReportFinanceSnapshot(@RequestBody @Valid AfterLeaseCheckReportFinanceREQ req);

    @ApiOperation("保存客户财务报表快照数据")
    @PostMapping("/afterlease/check/project/report/finance/snapshot/save")
    R<Void> saveReportFinanceSnapshot(@RequestBody @Valid AfterLeaseCheckReportFinanceSaveREQ req);

    @ApiOperation("获取检查报告中用户上传的财务数据文件")
    @PostMapping("/afterlease/check/project/report/file/finance/list")
    R<List<Pair<String, List<ProjReviewReportListRSP>>>> listFinanceDataFile(@RequestBody @Valid SinglePkREQ req);
}
