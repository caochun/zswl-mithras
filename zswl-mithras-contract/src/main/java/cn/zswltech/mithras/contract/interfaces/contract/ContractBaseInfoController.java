package cn.zswltech.mithras.contract.interfaces.contract;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.ContractCompareBusinessREQ;
import cn.zswltech.mithras.dto.contract.ContractCompareBusinessRSP;
import cn.zswltech.mithras.dto.contract.ContractOperationPrepareREQ;
import cn.zswltech.mithras.dto.contract.baseinfo.*;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListRSP;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.api.contract.ContractBaseInfoApi;
import cn.zswltech.mithras.contract.application.contract.ContractBaseInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ContractBaseInfoController implements ContractBaseInfoApi {
    @Resource
    private ContractBaseInfoApplicationService contractBaseInfoApplicationService;

    @Override
    public R<Void> prepareContractOperation(@RequestBody @Valid ContractOperationPrepareREQ req) {
        return contractBaseInfoApplicationService.prepareContractOperation(req);
    }

    @Override
    public R<Void> saveAdjustRemark(@RequestBody @Valid ContractAdjustRemarkREQ req) {
        return contractBaseInfoApplicationService.saveAdjustRemark(req);
    }

    @Override
    public R<Void> saveChangeRemark(@RequestBody @Valid ContractChangeRemarkREQ req) {
        return contractBaseInfoApplicationService.saveChangeRemark(req);
    }

    @Override
    public R<List<ProjEstablishVagueListRSP>> vague(@RequestBody @Valid ProjEstablishVagueListREQ req) {
        return contractBaseInfoApplicationService.vague(req);
    }

    @Override
    public R<ContractBaseInfoAddRSP> add(@RequestBody @Valid ContractBaseInfoAddREQ req) {
        return contractBaseInfoApplicationService.add(req);
    }

    @Override
    public R<Void> modify(@RequestBody @Valid ContractBaseInfoModifyREQ req) {
        return contractBaseInfoApplicationService.modify(req);
    }

    @Override
    public R<Void> modifyLeaseItem(@RequestBody @Valid ContractBaseInfoModifyREQ req) {
        return contractBaseInfoApplicationService.modifyLeaseItem(req);
    }

    @Override
    public R<PageR<ContractBaseInfoListRSP>> list(@RequestBody @Valid ContractBaseInfoListREQ req) {
        return contractBaseInfoApplicationService.list(req);
    }

    @Override
    public R<Void> remove(@RequestBody @Valid ContractBaseInfoRemoveREQ req) {
        return contractBaseInfoApplicationService.remove(req);
    }

    @Override
    public R<Void> cancel(@RequestBody @Valid ContractBaseInfoRemoveREQ req) {
        return contractBaseInfoApplicationService.cancel(req);
    }

    @Override
    public R<ContractBaseInfoDetailRSP> detail(@Valid ContractBaseInfoDetailREQ req) {
        return contractBaseInfoApplicationService.detail(req);
    }

    @Override
    public R<Void> updateActualLeaseDate(@RequestBody @Valid ContractActualLeaseDateREQ req) {
        return contractBaseInfoApplicationService.updateActualLeaseDate(req);
    }

    @Override
    public void exportContract() {
        contractBaseInfoApplicationService.exportContract();
    }

    @Override
    public R<List<ContractCompareBusinessRSP>> compareBusiness(@RequestBody @Valid ContractCompareBusinessREQ req) {
        return contractBaseInfoApplicationService.compareBusiness(req);
    }

    @Override
    public R<Boolean> checkCombinedIrr(@RequestBody @Valid ContractOperationPrepareREQ req) {
        return contractBaseInfoApplicationService.checkCombinedIrr(req);
    }
}
