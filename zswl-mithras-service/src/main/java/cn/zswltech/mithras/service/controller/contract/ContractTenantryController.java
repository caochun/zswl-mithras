package cn.zswltech.mithras.service.controller.contract;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.contract.ContractTenantryApi;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.tenantry.*;
import cn.zswltech.mithras.service.annotation.ContractChangeOther;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseModifySubAuthChecker;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseRemoveSubAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.contract.ContractTenantryMapper;
import cn.zswltech.mithras.service.service.contract.ContractTenantryService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;

/**
* @description 合同-租赁报价方案表
* @author vico
* @date 2022-08-12
*/
@RestController
public class ContractTenantryController implements ContractTenantryApi {

    @Resource
    private ContractTenantryService contractTenantryService;


    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseModifySubAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT, mapperClass = ContractTenantryMapper.class)
    public R<Void> modify(ContractTenantryModifyREQ req){
        contractTenantryService.modify(req);
        return R.ok();
    }

    @Override
    public R<List<ContractTenantryListRSP>> list(ContractIdListREQ req){
        return R.ok(contractTenantryService.list(req));
    }

    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseRemoveSubAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT, mapperClass = ContractTenantryMapper.class)
    public R<Void> remove(ContractTenantryRemoveREQ req){
        contractTenantryService.remove(req);
        return R.ok();
    }
}