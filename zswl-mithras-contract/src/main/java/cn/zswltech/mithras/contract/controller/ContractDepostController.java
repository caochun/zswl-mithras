package cn.zswltech.mithras.contract.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.depost.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import javax.validation.Valid;
import cn.zswltech.mithras.api.contract.ContractDepostApi;
import cn.zswltech.mithras.contract.application.ContractDepostApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ContractDepostController implements ContractDepostApi {
    @Resource
    private ContractDepostApplicationService contractDepostApplicationService;

    @Override
    public R check(@RequestBody @Valid ContractDepostREQ req) {
        return contractDepostApplicationService.check(req);
    }

    @Override
    public R depostInfo(@RequestParam("id") Long id) {
        return contractDepostApplicationService.depostInfo(id);
    }

    @Override
    public R rentList(@RequestBody @Valid ContractRetreatREQ req) {
        return contractDepostApplicationService.rentList(req);
    }

    @Override
    public R rentAdd(@RequestBody @Valid ContractDeductRentInfoREQ req) {
        return contractDepostApplicationService.rentAdd(req);
    }

    @Override
    public R rentDel(@RequestBody @Valid ContractDeductRentREQ req) {
        return contractDepostApplicationService.rentDel(req);
    }

    @Override
    public R getByContractId(@RequestBody @Valid ContractDepostREQ req) {
        return contractDepostApplicationService.getByContractId(req);
    }

    @Override
    public R getByCollectionId(@RequestParam("collectionId") String collectionId) {
        return contractDepostApplicationService.getByCollectionId(collectionId);
    }

    @Override
    public R downloadtemplete() {
        return contractDepostApplicationService.downloadtemplete();
    }

    @Override
    public R depostSave(@RequestBody @Valid ContractRetreatSubmitREQ req) {
        return contractDepostApplicationService.depostSave(req);
    }

    @Override
    public R submit(@RequestBody @Valid ContractRetreatSubmitREQ req) {
        return contractDepostApplicationService.submit(req);
    }

    @Override
    public R noticeCommit(@RequestParam("prepareId") String prepareId) {
        return contractDepostApplicationService.noticeCommit(prepareId);
    }
}
