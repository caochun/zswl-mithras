package cn.zswltech.mithras.api.projpricing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanExportREQ;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListCompareRSP;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListREQ;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListRSP;
import cn.zswltech.mithras.dto.projreview.cashflowplan.IRRCalculateResultRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;


@Api(tags = "项目定价-现金流计划相关接口")
public interface ProjPricingCashFlowPlanApi {
    @ApiOperation("下载现金流计划表模板")
    @GetMapping("/proj/pricing/cashflowplan/download")
    R<String> download();

    @ApiOperation("上传现金流计划表")
    @PostMapping("/proj/pricing/cashflowplan/upload")
    R<Void> upload(@RequestParam("file") MultipartFile file, @RequestParam("projPricingId") Long projPricingId);

    @ApiOperation("获取现金流计划表")
    @PostMapping("/proj/pricing/cashflowplan/list")
    R<List<ProjPricingCashFlowPlanListRSP>> list(@RequestBody @Valid ProjPricingCashFlowPlanListREQ projPricingCashFlowPlanListREQ);

    @ApiOperation("导出租金表")
    @PostMapping("/proj/pricing/cashflowplan/rent/export")
    void exportRent(@RequestBody @Valid ProjPricingCashFlowPlanExportREQ projPricingCashFlowPlanExportREQ);

    @ApiOperation("导出现金流表")
    @PostMapping("/proj/pricing/cashflowplan/cashflow/export")
    void exportCashFlow(@RequestBody @Valid ProjPricingCashFlowPlanExportREQ projPricingCashFlowPlanExportREQ);

    @ApiOperation("自动生成现金流计划表")
    @PostMapping("/proj/pricing/cashflowplan/cashflow/generate")
    R<Void> generate(@RequestBody @Valid SinglePkREQ singlePkREQ);

    @ApiOperation("计算IRR")
    @PostMapping("/proj/pricing/cashflowplan/irr/calculate")
    R<IRRCalculateResultRSP> calculateIRR(@RequestBody @Valid SinglePkREQ singlePkREQ);

    @ApiOperation("获取现金流计划表与评审会纪要比对")
    @PostMapping("/proj/pricing/cashflowplan/meet/minute/compare")
    R<List<ProjPricingCashFlowPlanListCompareRSP>> compare(@RequestBody @Valid ProjPricingCashFlowPlanListREQ projPricingCashFlowPlanListREQ);

}
