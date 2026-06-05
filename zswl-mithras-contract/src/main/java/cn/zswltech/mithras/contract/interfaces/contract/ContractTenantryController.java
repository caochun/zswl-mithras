package cn.zswltech.mithras.contract.interfaces.contract;

import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.tenantry.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.R;
import java.util.List;
import cn.zswltech.mithras.api.contract.ContractTenantryApi;
import cn.zswltech.mithras.contract.application.contract.ContractTenantryApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ContractTenantryController implements ContractTenantryApi {
    @Resource
    private ContractTenantryApplicationService contractTenantryApplicationService;

    @Override
    public R<Void> modify(@Valid ContractTenantryModifyREQ req) {
        return contractTenantryApplicationService.modify(req);
    }

    @Override
    public R<List<ContractTenantryListRSP>> list(@RequestBody @Valid ContractIdListREQ req) {
        return contractTenantryApplicationService.list(req);
    }

    @Override
    public R<Void> remove(@RequestBody @Valid ContractTenantryRemoveREQ req) {
        return contractTenantryApplicationService.remove(req);
    }
}
