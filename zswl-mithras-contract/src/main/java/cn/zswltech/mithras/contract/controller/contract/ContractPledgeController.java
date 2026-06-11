package cn.zswltech.mithras.contract.controller.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationRSP;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeAddREQ;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeListRSP;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeModifyREQ;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeRemoveREQ;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.api.contract.ContractPledgeApi;
import cn.zswltech.mithras.contract.application.contract.ContractPledgeApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ContractPledgeController implements ContractPledgeApi {
    @Resource
    private ContractPledgeApplicationService contractPledgeApplicationService;

    @Override
    public R<Boolean> add(@Valid ContractPledgeAddREQ req) {
        return contractPledgeApplicationService.add(req);
    }

    @Override
    public R<Void> modify(@Valid ContractPledgeModifyREQ req) {
        return contractPledgeApplicationService.modify(req);
    }

    @Override
    public R<List<ContractPledgeListRSP>> list(@RequestBody @Valid ContractIdListREQ req) {
        return contractPledgeApplicationService.list(req);
    }

    @Override
    public R<Void> remove(@RequestBody @Valid ContractPledgeRemoveREQ req) {
        return contractPledgeApplicationService.remove(req);
    }

    @Override
    public R<Void> generatePledgeContractCode(@RequestBody @Valid ContractSingleIdREQ contractSingleIdREQ) {
        return contractPledgeApplicationService.generatePledgeContractCode(contractSingleIdREQ);
    }

    @Override
    public R<String> downloadPledgeItemTemplate() {
        return contractPledgeApplicationService.downloadPledgeItemTemplate();
    }

    @Override
    public R<List<ContractRelationRSP>> relation(@RequestBody @Valid ContractRelationREQ req) {
        return contractPledgeApplicationService.relation(req);
    }
}
