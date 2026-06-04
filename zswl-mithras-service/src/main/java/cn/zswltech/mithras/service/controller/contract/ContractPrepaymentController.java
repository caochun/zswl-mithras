package cn.zswltech.mithras.service.controller.contract;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.contract.ContractPrepaymentApi;
import cn.zswltech.mithras.dto.contract.ContractCanChangeRSP;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.prepayment.ContractPrepaymentAddREQ;
import cn.zswltech.mithras.dto.contract.prepayment.ContractPrepaymentDetailRSP;
import cn.zswltech.mithras.dto.contract.prepayment.ContractPrepaymentModifyREQ;
import cn.zswltech.mithras.service.annotation.ContractChangeOther;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseAddSubAuthChecker;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseModifySubAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractChangeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractAccountMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractPrepaymentMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.application.ContractPrepaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;

/**
 * @ClassName ContractPrepaymentController
 * @Description
 * @Author jackerhe
 * @Date 2022/8/25 2:31 下午
 * @Version 1.0
 **/
@RestController
public class ContractPrepaymentController implements ContractPrepaymentApi {

    @Autowired
    private ContractPrepaymentService contractPrepaymentService;
    @Autowired
    private ContractBaseInfoService baseInfoService;

    @Override
//    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseAddSubAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT)
    public R<Void> add(@Valid ContractPrepaymentAddREQ req) {
        chackCanChange(req.getContractId());
        contractPrepaymentService.add(req);
        // 状态维护统一到ContractOperationPrepare中了
//        baseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_UNCOMMIT.name(), ContractChangeTypeEnum.EARLY_REPAYMENT.name(), req.getContractId());
        return R.ok();
    }

    @Override
    @ContractChangeOther(twoStatus = ContractChangeTypeEnum.EARLY_REPAYMENT)
//    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseModifySubAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT, mapperClass = ContractPrepaymentMapper.class)
    public R<Void> modify(@Valid ContractPrepaymentModifyREQ req) {
        //chackCanChange(req.getContractId());
        contractPrepaymentService.update(req);
        // 状态维护统一到ContractOperationPrepare中了
        //baseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_UNCOMMIT.name(), ContractChangeTypeEnum.EARLY_REPAYMENT.name(), req.getContractId());
        return R.ok();
    }

    @Override
    public R<ContractPrepaymentDetailRSP> list(@Valid ContractIdListREQ req) {
       return R.ok(BeanUtil.copyProperties(contractPrepaymentService.getList(req), ContractPrepaymentDetailRSP.class));
    }

    @Override
    public R<ContractPrepaymentAddREQ> calculation(ContractPrepaymentAddREQ req) {
        contractPrepaymentService.calculation(req);
        return R.ok(req);
    }

    private void chackCanChange(Long contractId){
        ContractCanChangeRSP rsp = baseInfoService.canUpdateContractProcessStatus(Collections.singletonList(ContractProcessStatusEnum.CHANGE_UNCOMMIT.name()), contractId);
        if(Boolean.FALSE.equals(rsp.getCanProcess())){
            throw new MithrasException(rsp.getMessage());
        }
        if(ObjectUtil.isNotEmpty(rsp.getTwoStatus()) && !ContractChangeTypeEnum.EARLY_REPAYMENT.name().equals(rsp.getTwoStatus())){
            throw new MithrasException("合同变更处于" + ContractChangeTypeEnum.of(rsp.getTwoStatus()).display);
        }

    }

}
