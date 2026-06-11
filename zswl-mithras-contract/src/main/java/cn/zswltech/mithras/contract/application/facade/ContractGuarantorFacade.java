package cn.zswltech.mithras.contract.application.facade;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.contract.application.ContractGuarantorApplicationService;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.guarantor.*;
import cn.zswltech.mithras.dto.contract.ContractRelationREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationRSP;
import cn.zswltech.mithras.contract.annotation.ContractChangeOther;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.contract.application.auth.ContractBaseAddSubAuthChecker;
import cn.zswltech.mithras.contract.application.auth.ContractBaseModifyMainAuthChecker;
import cn.zswltech.mithras.contract.application.auth.ContractBaseModifySubAuthChecker;
import cn.zswltech.mithras.contract.application.auth.ContractBaseRemoveSubAuthChecker;
import cn.zswltech.mithras.contract.mapper.contract.ContractGuarantorMapper;
import cn.zswltech.mithras.contract.core.ContractGuarantorService;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Service;

/**
* @description 合同-担保措施
* @author vico
* @date 2022-08-12
*/
@Service
public class ContractGuarantorFacade implements ContractGuarantorApplicationService {

    @Resource
    private ContractGuarantorService contractGuarantorService;

    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseModifyMainAuthChecker.class, businessModule = "CONTRACT", mapperClass = ContractGuarantorMapper.class)
    public R<Void> generateGuarantorContractCode(@Valid ContractSingleIdREQ contractSingleIdREQ) {
        contractGuarantorService.generateGuarantorContractCode(contractSingleIdREQ.getContractId());
        return R.ok();
    }

    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseAddSubAuthChecker.class, businessModule = "CONTRACT")
    public R<Boolean> add(ContractGuarantorAddREQ req) {
        return R.ok(contractGuarantorService.add(req));
    }

    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseModifySubAuthChecker.class, businessModule = "CONTRACT", mapperClass = ContractGuarantorMapper.class)
    public R<Void> modify(ContractGuarantorModifyREQ req){
        contractGuarantorService.modify(req);
        return R.ok();
    }

    @Override
    public R<List<ContractRelationRSP>> relation(@Valid ContractRelationREQ req) {
        return R.ok(contractGuarantorService.contractByclient(req));
    }

    @Override
    public R<List<ContractGuarantorListRSP>> list(ContractIdListREQ req){
        return R.ok(contractGuarantorService.list(req));
    }

    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseRemoveSubAuthChecker.class, businessModule = "CONTRACT", mapperClass = ContractGuarantorMapper.class)
    public R<Void> remove(ContractGuarantorRemoveREQ req){
        contractGuarantorService.remove(req);
        return R.ok();
    }

}
