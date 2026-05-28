package cn.zswltech.mithras.api.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractRentPhaseRSP;
import cn.zswltech.mithras.dto.contract.rent.*;
import cn.zswltech.mithras.dto.projreview.cashflowplan.IRRCalculateResultRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/8/12
 * @description
 */
@Api(tags = "合同管理-租金表相关接口")
public interface ContractRentApi {
    @ApiOperation("概算租金表测算IRR")
    @PostMapping("/contract/rent/estimate/irr/calculate")
    R<IRRCalculateResultRSP> calculateIRR(@RequestBody @Valid SinglePkREQ singlePkREQ);

    @ApiOperation("系统生成概算现金流")
    @PostMapping("/contract/rent/estimate/generate")
    R<Void> generateEstimate(@RequestBody @Valid ContractRentEstimateGenerateREQ req);

    @ApiOperation("下载概算租金表模板")
    @GetMapping("/contract/rent/estimate/template/download")
    R<String> downloadEstimateRentTemplate();

    @ApiOperation("导入概算租金表")
    @PostMapping("/contract/rent/estimate/import")
    R<Void> importEstimateRent(@Valid ContractRentEstimateImportREQ contractRentEstimateImportREQ);

    @ApiOperation("导出概算租金表")
    @PostMapping("/contract/rent/estimate/export")
    void exportEstimateRent(@RequestBody @Valid ContractRentEstimateExportREQ contractRentEstimateExportREQ);

    @ApiOperation("导出概算现金流表")
    @PostMapping("/contract/cashflow/estimate/export")
    void exportEstimateCashFlow(@RequestBody @Valid ContractRentEstimateExportREQ contractRentEstimateExportREQ);

    @ApiOperation("获取概算租金表")
    @PostMapping("/contract/rent/estimate/list")
    R<ContractRentEstimateListRSP> listEstimateRent(@RequestBody @Valid ContractSingleIdREQ contractSingleIdREQ);

    @ApiOperation("实际租金表测算IRR")
    @PostMapping("/contract/rent/actual/irr/calculate")
    R<IRRCalculateResultRSP> calculateActualIRR(@RequestBody @Valid SinglePkREQ singlePkREQ);

    @ApiOperation("导入实际租金表")
    @PostMapping("/contract/rent/actual/import")
    R<Void> importActualRent(@Valid ContractRentActualImportREQ contractRentActualImportREQ);

    @ApiOperation("获取实际租金表")
    @PostMapping("/contract/rent/actual/list")
    R<List<ContractRentActualListRSP>> listActualRent(@RequestBody @Valid ContractSingleIdREQ contractSingleIdREQ);

    @ApiOperation("导出实际租金表")
    @PostMapping("/contract/rent/actual/export")
    void exportActualRent(@RequestBody @Valid ContractRentActualExportREQ contractRentActualExportREQ);

    @ApiOperation("导出实际现金流表")
    @PostMapping("/contract/cashflow/actual/export")
    void exportActualCashFlow(@RequestBody @Valid ContractRentActualExportREQ contractRentActualExportREQ);

    @ApiOperation("下载实际租金表模板")
    @GetMapping("/contract/rent/actual/template/download")
    R<String> downloadActualRentTemplate();

    @ApiOperation("查询合同下租金期项")
    @PostMapping("/contract/price/phase/list")
    R<List<ContractRentPhaseRSP>> rentPhaseList(@RequestBody @Valid ContractPriceDetailREQ req);

    @ApiOperation("综合IRR")
    @PostMapping("/contract/rent/combined/irr/calculate")
    R<BigDecimal> calculateCombinedIRR(@RequestBody @Valid ContractSingleIdREQ contractSingleIdREQ);

}
