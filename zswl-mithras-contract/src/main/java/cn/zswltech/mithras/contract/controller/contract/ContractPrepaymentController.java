package cn.zswltech.mithras.contract.controller.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.prepayment.ContractPrepaymentAddREQ;
import cn.zswltech.mithras.dto.contract.prepayment.ContractPrepaymentDetailRSP;
import cn.zswltech.mithras.dto.contract.prepayment.ContractPrepaymentModifyREQ;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.contract.ContractPrepaymentApi;
import cn.zswltech.mithras.contract.application.contract.ContractPrepaymentApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ContractPrepaymentController implements ContractPrepaymentApi {
    @Resource
    private ContractPrepaymentApplicationService contractPrepaymentApplicationService;

    @Override
    public R<Void> add(@RequestBody @Valid ContractPrepaymentAddREQ req) {
        return contractPrepaymentApplicationService.add(req);
    }

    @Override
    public R<Void> modify(@RequestBody @Valid ContractPrepaymentModifyREQ req) {
        return contractPrepaymentApplicationService.modify(req);
    }

    @Override
    public R<ContractPrepaymentDetailRSP> list(@RequestBody @Valid ContractIdListREQ req) {
        return contractPrepaymentApplicationService.list(req);
    }

    @Override
    public R<ContractPrepaymentAddREQ> calculation(@RequestBody ContractPrepaymentAddREQ req) {
        return contractPrepaymentApplicationService.calculation(req);
    }
}
