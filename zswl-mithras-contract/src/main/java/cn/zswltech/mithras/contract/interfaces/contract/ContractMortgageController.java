package cn.zswltech.mithras.contract.interfaces.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationRSP;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageAddREQ;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageListRSP;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageModifyREQ;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageRemoveREQ;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.api.contract.ContractMortgageApi;
import cn.zswltech.mithras.contract.application.contract.ContractMortgageApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ContractMortgageController implements ContractMortgageApi {
    @Resource
    private ContractMortgageApplicationService contractMortgageApplicationService;

    @Override
    public R<Void> generateMortgageContractCode(@RequestBody @Valid ContractSingleIdREQ contractSingleIdREQ) {
        return contractMortgageApplicationService.generateMortgageContractCode(contractSingleIdREQ);
    }

    @Override
    public R<Void> add(@Valid ContractMortgageAddREQ req) {
        return contractMortgageApplicationService.add(req);
    }

    @Override
    public R<Void> modify(@Valid ContractMortgageModifyREQ req) {
        return contractMortgageApplicationService.modify(req);
    }

    @Override
    public R<List<ContractRelationRSP>> relation(@RequestBody @Valid ContractRelationREQ req) {
        return contractMortgageApplicationService.relation(req);
    }

    @Override
    public R<List<ContractMortgageListRSP>> list(@RequestBody @Valid ContractIdListREQ req) {
        return contractMortgageApplicationService.list(req);
    }

    @Override
    public R<Void> remove(@RequestBody @Valid ContractMortgageRemoveREQ req) {
        return contractMortgageApplicationService.remove(req);
    }

    @Override
    public R<String> downloadTemplate() {
        return contractMortgageApplicationService.downloadTemplate();
    }
}
