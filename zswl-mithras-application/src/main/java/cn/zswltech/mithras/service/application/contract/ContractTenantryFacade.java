package cn.zswltech.mithras.service.application.contract;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.contract.application.contract.ContractTenantryApplicationService;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.tenantry.*;
import cn.zswltech.mithras.contract.annotation.ContractChangeOther;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseModifySubAuthChecker;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseRemoveSubAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractTenantryMapper;
import cn.zswltech.mithras.contract.core.application.ContractTenantryService;
import javax.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

/**
* @description 合同-租赁报价方案表
* @author vico
* @date 2022-08-12
*/
@Service
public class ContractTenantryFacade implements ContractTenantryApplicationService {

    @Resource
    private ContractTenantryService contractTenantryService;


    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseModifySubAuthChecker.class, businessModule = "CONTRACT", mapperClass = ContractTenantryMapper.class)
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
    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseRemoveSubAuthChecker.class, businessModule = "CONTRACT", mapperClass = ContractTenantryMapper.class)
    public R<Void> remove(ContractTenantryRemoveREQ req){
        contractTenantryService.remove(req);
        return R.ok();
    }
}