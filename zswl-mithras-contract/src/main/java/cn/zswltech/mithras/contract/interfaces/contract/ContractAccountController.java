package cn.zswltech.mithras.contract.interfaces.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.account.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.api.contract.ContractAccountApi;
import cn.zswltech.mithras.contract.application.contract.ContractAccountApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ContractAccountController implements ContractAccountApi {
    @Resource
    private ContractAccountApplicationService contractAccountApplicationService;

    @Override
    public R<Void> add(@RequestBody @Valid ContractAccountAddREQ req) {
        return contractAccountApplicationService.add(req);
    }

    @Override
    public R<Void> modify(@RequestBody @Valid List<ContractAccountModifyREQ> req) {
        return contractAccountApplicationService.modify(req);
    }

    @Override
    public R<List<ContractAccountListRSP>> list(@RequestBody @Valid ContractAccountListREQ req) {
        return contractAccountApplicationService.list(req);
    }

    @Override
    public R<Void> remove(@RequestBody @Valid ContractAccountRemoveREQ req) {
        return contractAccountApplicationService.remove(req);
    }
}
