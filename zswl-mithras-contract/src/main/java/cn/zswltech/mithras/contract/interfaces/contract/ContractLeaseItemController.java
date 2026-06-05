package cn.zswltech.mithras.contract.interfaces.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.leaseitem.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.contract.ContractLeaseItemApi;
import cn.zswltech.mithras.contract.application.contract.ContractLeaseItemApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ContractLeaseItemController implements ContractLeaseItemApi {
    @Resource
    private ContractLeaseItemApplicationService contractLeaseItemApplicationService;

    @Override
    public R<Void> importExcel(@Valid ContractLeaseItemImportREQ contractLeaseItemImportREQ) {
        return contractLeaseItemApplicationService.importExcel(contractLeaseItemImportREQ);
    }

    @Override
    public R<ContractLeaseItemListRSP> listLeaseItem(@RequestBody @Valid ContractLeaseItemListREQ req) {
        return contractLeaseItemApplicationService.listLeaseItem(req);
    }

    @Override
    public R<String> downloadTemplate() {
        return contractLeaseItemApplicationService.downloadTemplate();
    }

    @Override
    public R<ContractPreChooseLeaseItemRSP> getPreChooseLeaseItem(@RequestBody @Valid ContractPreChooseLeaseItemREQ req) {
        return contractLeaseItemApplicationService.getPreChooseLeaseItem(req);
    }

    @Override
    public R<Void> chooseLeaseItem(@RequestBody @Valid ContractChooseLeaseItemREQ req) {
        return contractLeaseItemApplicationService.chooseLeaseItem(req);
    }

    @Override
    public void exportExcel(@RequestBody @Valid ContractLeaseItemExportREQ req) {
        contractLeaseItemApplicationService.exportExcel(req);
    }

    @Override
    public R<Void> saveLeaseItemTotalAmount(@RequestBody @Valid ContractLeaseItemTotalAmountREQ req) {
        return contractLeaseItemApplicationService.saveLeaseItemTotalAmount(req);
    }

    @Override
    public R<Void> checkLeaseItemInProcess(@RequestBody @Valid ContractSingleIdREQ req) {
        return contractLeaseItemApplicationService.checkLeaseItemInProcess(req);
    }
}
