package cn.zswltech.mithras.contract.interfaces.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractRentPhaseRSP;
import cn.zswltech.mithras.dto.contract.rent.*;
import cn.zswltech.mithras.dto.projreview.cashflowplan.IRRCalculateResultRSP;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import cn.zswltech.mithras.api.contract.ContractRentApi;
import cn.zswltech.mithras.contract.application.contract.ContractRentApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ContractRentController implements ContractRentApi {
    @Resource
    private ContractRentApplicationService contractRentApplicationService;

    @Override
    public R<IRRCalculateResultRSP> calculateIRR(@RequestBody @Valid SinglePkREQ singlePkREQ) {
        return contractRentApplicationService.calculateIRR(singlePkREQ);
    }

    @Override
    public R<Void> generateEstimate(@RequestBody @Valid ContractRentEstimateGenerateREQ req) {
        return contractRentApplicationService.generateEstimate(req);
    }

    @Override
    public R<String> downloadEstimateRentTemplate() {
        return contractRentApplicationService.downloadEstimateRentTemplate();
    }

    @Override
    public R<Void> importEstimateRent(@Valid ContractRentEstimateImportREQ contractRentEstimateImportREQ) {
        return contractRentApplicationService.importEstimateRent(contractRentEstimateImportREQ);
    }

    @Override
    public void exportEstimateRent(@RequestBody @Valid ContractRentEstimateExportREQ contractRentEstimateExportREQ) {
        contractRentApplicationService.exportEstimateRent(contractRentEstimateExportREQ);
    }

    @Override
    public void exportEstimateCashFlow(@RequestBody @Valid ContractRentEstimateExportREQ contractRentEstimateExportREQ) {
        contractRentApplicationService.exportEstimateCashFlow(contractRentEstimateExportREQ);
    }

    @Override
    public R<ContractRentEstimateListRSP> listEstimateRent(@RequestBody @Valid ContractSingleIdREQ contractSingleIdREQ) {
        return contractRentApplicationService.listEstimateRent(contractSingleIdREQ);
    }

    @Override
    public R<IRRCalculateResultRSP> calculateActualIRR(@RequestBody @Valid SinglePkREQ singlePkREQ) {
        return contractRentApplicationService.calculateActualIRR(singlePkREQ);
    }

    @Override
    public R<Void> importActualRent(@Valid ContractRentActualImportREQ contractRentActualImportREQ) {
        return contractRentApplicationService.importActualRent(contractRentActualImportREQ);
    }

    @Override
    public R<List<ContractRentActualListRSP>> listActualRent(@RequestBody @Valid ContractSingleIdREQ contractSingleIdREQ) {
        return contractRentApplicationService.listActualRent(contractSingleIdREQ);
    }

    @Override
    public void exportActualRent(@RequestBody @Valid ContractRentActualExportREQ contractRentActualExportREQ) {
        contractRentApplicationService.exportActualRent(contractRentActualExportREQ);
    }

    @Override
    public void exportActualCashFlow(@RequestBody @Valid ContractRentActualExportREQ contractRentActualExportREQ) {
        contractRentApplicationService.exportActualCashFlow(contractRentActualExportREQ);
    }

    @Override
    public R<String> downloadActualRentTemplate() {
        return contractRentApplicationService.downloadActualRentTemplate();
    }

    @Override
    public R<List<ContractRentPhaseRSP>> rentPhaseList(@RequestBody @Valid ContractPriceDetailREQ req) {
        return contractRentApplicationService.rentPhaseList(req);
    }

    @Override
    public R<BigDecimal> calculateCombinedIRR(@RequestBody @Valid ContractSingleIdREQ contractSingleIdREQ) {
        return contractRentApplicationService.calculateCombinedIRR(contractSingleIdREQ);
    }
}
