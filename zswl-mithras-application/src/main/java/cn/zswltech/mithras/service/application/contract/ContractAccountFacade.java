package cn.zswltech.mithras.service.application.contract;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.contract.application.contract.ContractAccountApplicationService;
import cn.zswltech.mithras.dto.contract.account.*;
import cn.zswltech.mithras.contract.annotation.ContractChangeOther;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseAddSubAuthChecker;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseModifySubAuthChecker;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseRemoveSubAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractAccountMapper;
import cn.zswltech.mithras.contract.core.application.ContractAccountService;
import javax.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;


/**
* @description 合同-收款账户表
* @author vico
* @date 2022-08-12
*/
@Service
public class ContractAccountFacade implements ContractAccountApplicationService {

    @Resource
    private ContractAccountService contractAccountService;

    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseAddSubAuthChecker.class, businessModule = "CONTRACT")
    public R<Void> add(ContractAccountAddREQ req) {
        contractAccountService.add(req);
        return R.ok();
    }

    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseModifySubAuthChecker.class, businessModule = "CONTRACT", mapperClass = ContractAccountMapper.class)
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
    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseRemoveSubAuthChecker.class, businessModule = "CONTRACT", mapperClass = ContractAccountMapper.class)
    public R<Void> remove(ContractAccountRemoveREQ req){
        contractAccountService.remove(req);
        return R.ok();
    }

}