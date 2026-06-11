package cn.zswltech.mithras.contract.controller;

import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.guarantor.*;
import cn.zswltech.mithras.dto.contract.ContractRelationREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationRSP;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.R;
import java.util.List;
import cn.zswltech.mithras.api.contract.ContractGuarantorApi;
import cn.zswltech.mithras.contract.application.ContractGuarantorApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ContractGuarantorController implements ContractGuarantorApi {
    @Resource
    private ContractGuarantorApplicationService contractGuarantorApplicationService;

    @Override
    public R<Void> generateGuarantorContractCode(@RequestBody @Valid ContractSingleIdREQ contractSingleIdREQ) {
        return contractGuarantorApplicationService.generateGuarantorContractCode(contractSingleIdREQ);
    }

    @Override
    public R<Boolean> add(@Valid ContractGuarantorAddREQ req) {
        return contractGuarantorApplicationService.add(req);
    }

    @Override
    public R<Void> modify(@Valid ContractGuarantorModifyREQ req) {
        return contractGuarantorApplicationService.modify(req);
    }

    @Override
    public R<List<ContractRelationRSP>> relation(@RequestBody @Valid ContractRelationREQ req) {
        return contractGuarantorApplicationService.relation(req);
    }

    @Override
    public R<List<ContractGuarantorListRSP>> list(@RequestBody @Valid ContractIdListREQ req) {
        return contractGuarantorApplicationService.list(req);
    }

    @Override
    public R<Void> remove(@RequestBody @Valid ContractGuarantorRemoveREQ req) {
        return contractGuarantorApplicationService.remove(req);
    }
}
