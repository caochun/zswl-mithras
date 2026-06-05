package cn.zswltech.mithras.service.controller.contract;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.contract.ContractAccountApi;
import cn.zswltech.mithras.dto.contract.account.*;
import cn.zswltech.mithras.contract.annotation.ContractChangeOther;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseAddSubAuthChecker;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseModifySubAuthChecker;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseRemoveSubAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractAccountMapper;
import cn.zswltech.mithras.contract.core.application.ContractAccountService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;


/**
* @description 合同-收款账户表
* @author vico
* @date 2022-08-12
*/
@RestController
public class ContractAccountController implements ContractAccountApi {

    @Resource
    private ContractAccountService contractAccountService;

    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseAddSubAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT)
    public R<Void> add(ContractAccountAddREQ req) {
        contractAccountService.add(req);
        return R.ok();
    }

    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseModifySubAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT, mapperClass = ContractAccountMapper.class)
    public R<Void> modify(List<ContractAccountModifyREQ> req){
        contractAccountService.modify(req);
        return R.ok();
    }

    @Override
    public R<List<ContractAccountListRSP>> list(ContractAccountListREQ req){
        return R.ok(contractAccountService.list(req));
    }

    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseRemoveSubAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT, mapperClass = ContractAccountMapper.class)
    public R<Void> remove(ContractAccountRemoveREQ req){
        contractAccountService.remove(req);
        return R.ok();
    }

}