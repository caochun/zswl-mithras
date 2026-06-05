package cn.zswltech.mithras.contract.interfaces.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.settle.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.api.contract.ContractSettleApi;
import cn.zswltech.mithras.contract.application.contract.ContractSettleApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ContractSettleController implements ContractSettleApi {
    @Resource
    private ContractSettleApplicationService contractSettleApplicationService;

    @Override
    public R<Long> savePlanNormal(@RequestBody @Valid ContractSettlePlanNormalREQ contractSettlePlanNormalREQ) {
        return contractSettleApplicationService.savePlanNormal(contractSettlePlanNormalREQ);
    }

    @Override
    public R<Long> savePlanInAdvance(@RequestBody @Valid ContractSettlePlanInAdvanceREQ contractSettlePlanInAdvanceREQ) {
        return contractSettleApplicationService.savePlanInAdvance(contractSettlePlanInAdvanceREQ);
    }

    @Override
    public R<ContractSettlePlanDetailRSP> getLatestSettlePlan(@RequestBody @Valid ContractSettlePlanDetailREQ contractSettlePlanDetailREQ) {
        return contractSettleApplicationService.getLatestSettlePlan(contractSettlePlanDetailREQ);
    }

    @Override
    public R<Void> uploadExtraFile(@Valid ContractSettleExtraFileUploadREQ contractSettleExtraFileUploadREQ) {
        return contractSettleApplicationService.uploadExtraFile(contractSettleExtraFileUploadREQ);
    }

    @Override
    public R<Void> removeExtraFile(@RequestBody @Valid ContractSettleExtraFileRemoveREQ contractSettleExtraFileRemoveREQ) {
        return contractSettleApplicationService.removeExtraFile(contractSettleExtraFileRemoveREQ);
    }

    @Override
    public R<List<ContractSettleExtraFileRSP>> listSettleExtraFile(@RequestBody @Valid ContractSingleIdREQ contractSingleIdREQ) {
        return contractSettleApplicationService.listSettleExtraFile(contractSingleIdREQ);
    }
}
