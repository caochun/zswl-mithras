package cn.zswltech.mithras.api.projreview;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.IRRCalculateResultRSP;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanExportREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListRSP;
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
 * @author dingqi
 * @date 2022/8/1
 * @description
 */
@Api(tags = "项目评审-现金流计划相关接口")
public interface ProjReviewCashFlowPlanApi {
    @ApiOperation("下载现金流计划表模板")
    @GetMapping("/proj/review/cashflowplan/download")
    R<String> download();

    @ApiOperation("上传现金流计划表")
    @PostMapping("/proj/review/cashflowplan/upload")
    R<Void> upload(@RequestParam("file") MultipartFile file, @RequestParam("projReviewId") Long projReviewId);

    @ApiOperation("获取现金流计划表")
    @PostMapping("/proj/review/cashflowplan/list")
    R<List<ProjReviewCashFlowPlanListRSP>> list(@RequestBody @Valid ProjReviewCashFlowPlanListREQ projReviewCashFlowPlanListREQ);

    @ApiOperation("导出租金表")
    @PostMapping("/proj/review/cashflowplan/rent/export")
    void exportRent(@RequestBody @Valid ProjReviewCashFlowPlanExportREQ projReviewCashFlowPlanExportREQ);

    @ApiOperation("导出现金流表")
    @PostMapping("/proj/review/cashflowplan/cashflow/export")
    void exportCashFlow(@RequestBody @Valid ProjReviewCashFlowPlanExportREQ projReviewCashFlowPlanExportREQ);

    @ApiOperation("自动生成现金流计划表")
    @PostMapping("/proj/review/cashflowplan/cashflow/generate")
    R<Void> generate(@RequestBody @Valid SinglePkREQ singlePkREQ);

    @ApiOperation("计算IRR")
    @PostMapping("/proj/review/cashflowplan/irr/calculate")
    R<IRRCalculateResultRSP> calculateIRR(@RequestBody @Valid SinglePkREQ singlePkREQ);
}
