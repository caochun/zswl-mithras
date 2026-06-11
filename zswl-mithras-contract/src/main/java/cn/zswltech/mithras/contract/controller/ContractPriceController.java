package cn.zswltech.mithras.contract.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.price.ContractIRRSaveREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.contract.price.ContractPriceModifyREQ;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.contract.ContractPriceApi;
import cn.zswltech.mithras.contract.application.ContractPriceApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ContractPriceController implements ContractPriceApi {
    @Resource
    private ContractPriceApplicationService contractPriceApplicationService;

    @Override
    public R<Void> modify(@RequestBody @Valid ContractPriceModifyREQ req) {
        return contractPriceApplicationService.modify(req);
    }

    @Override
    public R<ContractPriceDetailRSP> detail(@RequestBody @Valid ContractPriceDetailREQ req) {
        return contractPriceApplicationService.detail(req);
    }

    @Override
    public R<ContractPriceDetailRSP> oldDetail(@RequestBody @Valid ContractPriceDetailREQ req) {
        return contractPriceApplicationService.oldDetail(req);
    }

    @Override
    public R<Void> saveIrrPercent(@RequestBody @Valid ContractIRRSaveREQ req) {
        return contractPriceApplicationService.saveIrrPercent(req);
    }
}
