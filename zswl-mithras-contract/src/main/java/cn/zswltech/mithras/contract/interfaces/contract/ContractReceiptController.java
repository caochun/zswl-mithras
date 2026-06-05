package cn.zswltech.mithras.contract.interfaces.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.receipt.*;
import cn.zswltech.mithras.dto.contract.rent.ContractReceiptComputeActualTaxRSP;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.api.contract.ContractReceiptApi;
import cn.zswltech.mithras.contract.application.contract.ContractReceiptApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ContractReceiptController implements ContractReceiptApi {
    @Resource
    private ContractReceiptApplicationService contractReceiptApplicationService;

    @Override
    public R<Void> remove(@RequestBody @Valid ContractReceiptRemoveREQ contractReceiptRemoveREQ) {
        return contractReceiptApplicationService.remove(contractReceiptRemoveREQ);
    }

    @Override
    public R<Void> updateActualIRR(@RequestBody @Valid ContractReceiptUpdateIrrREQ req) {
        return contractReceiptApplicationService.updateActualIRR(req);
    }

    @Override
    public R<Void> updateActualTax(@RequestBody @Valid ContractReceiptUpdateActualTaxREQ req) {
        return contractReceiptApplicationService.updateActualTax(req);
    }

    @Override
    public R<List<ContractReceiptComputeActualTaxRSP>> computeFinancialCosts(@RequestBody @Valid ContractReceiptQueryActualTaxREQ req) {
        return contractReceiptApplicationService.computeFinancialCosts(req);
    }

    @Override
    public R<Void> generate(@RequestBody @Valid ContractReceiptQueryActualTaxREQ req) throws Exception {
        return contractReceiptApplicationService.generate(req);
    }

    @Override
    public R<Void> updateActualStartDate(@RequestBody @Valid ContractReceiptUpdateStartDateREQ req) {
        return contractReceiptApplicationService.updateActualStartDate(req);
    }

    @Override
    public R<Void> updateActualLeaseDate(@RequestBody @Valid ContractReceiptUpdateStartDateREQ req) {
        return contractReceiptApplicationService.updateActualLeaseDate(req);
    }

    @Override
    public R<List<ContractReceiptInfoRSP>> listByContractId(@RequestBody @Valid ContractSingleIdREQ req) {
        return contractReceiptApplicationService.listByContractId(req);
    }
}
