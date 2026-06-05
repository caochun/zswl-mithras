package cn.zswltech.mithras.service.application.contract;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.contract.application.contract.ContractPriceApplicationService;
import cn.zswltech.mithras.dto.contract.price.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseModifyMainAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.service.contract.ContractPriceService;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Service;

/**
* @description 合同-租赁报价方案表
* @author vico
* @date 2022-08-12
*/
@Service
public class ContractPriceFacade implements ContractPriceApplicationService {

    @Resource
    private ContractPriceService priceService;


    @Override
    @DataAuthCheck(keyFieldName = "aocPriceModifyREQ.contractId,factoringPriceModifyREQ.contractId,leasePriceModifyREQ.contractId",
            checkerClass = ContractBaseModifyMainAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT)
    public R<Void> modify(ContractPriceModifyREQ req) {
        priceService.modify(req);
        return R.ok();
    }
    @Override
    public R<ContractPriceDetailRSP> detail(ContractPriceDetailREQ req) {
        return R.ok(priceService.detail(req));
    }

    @Override
    public R<ContractPriceDetailRSP> oldDetail(@Valid ContractPriceDetailREQ req) {
        return R.ok(priceService.editionDetail(req));
    }

    @Override
    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseModifyMainAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT)
    public R<Void> saveIrrPercent(@Valid ContractIRRSaveREQ req) {
        priceService.saveIrr(req.getContractId(), req.getIrrPercent());
        return R.ok();
    }


}