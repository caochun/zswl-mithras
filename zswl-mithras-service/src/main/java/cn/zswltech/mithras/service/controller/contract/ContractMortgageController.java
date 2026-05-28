package cn.zswltech.mithras.service.controller.contract;

import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.contract.ContractMortgageApi;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationRSP;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageAddREQ;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageListRSP;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageModifyREQ;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageRemoveREQ;
import cn.zswltech.mithras.service.annotation.ContractChangeOther;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseAddSubAuthChecker;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseModifyMainAuthChecker;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseModifySubAuthChecker;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseRemoveSubAuthChecker;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.contract.ContractMortgageMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractMortgageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
* @description 合同-抵押措施
* @author vico
* @date 2022-08-12
*/
@RestController
@Slf4j
public class ContractMortgageController implements ContractMortgageApi {

    @Resource
    private ContractMortgageService contractMortgageService;

    @Resource
    private HttpServletResponse httpServletResponse;

    @Resource
    private OssClient ossClient;

    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseModifyMainAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT, mapperClass = ContractMortgageMapper.class)
    public R<Void> generateMortgageContractCode(@Valid ContractSingleIdREQ contractSingleIdREQ) {
        contractMortgageService.generateMortgageContractCode(contractSingleIdREQ.getContractId());
        return R.ok();
    }

    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseAddSubAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT)
    public R<Void> add(ContractMortgageAddREQ req) {
        contractMortgageService.add(req);
        return R.ok();
    }

    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseModifySubAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT, mapperClass = ContractMortgageMapper.class)
    public R<Void> modify(ContractMortgageModifyREQ req){
        contractMortgageService.modify(req);
        return R.ok();
    }

    @Override
    public R<List<ContractRelationRSP>> relation(@Valid ContractRelationREQ req) {
        return R.ok(contractMortgageService.contractByclient(req));
    }

    @Override
    public R<List<ContractMortgageListRSP>> list(@Valid ContractIdListREQ req) {
        return R.ok(contractMortgageService.list(req));
    }


    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseRemoveSubAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT, mapperClass = ContractMortgageMapper.class)
    public R<Void> remove(ContractMortgageRemoveREQ req){
        contractMortgageService.remove(req);
        return R.ok();
    }

    @Override
    public R<String> downloadTemplate() {
        try {
            /*httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(GlobalConstants.TEMPLATE_OSS_NAME_MORTGAGE_ITEM, StandardCharsets.UTF_8.name()));
            ossClient.downLoad(httpServletResponse.getOutputStream(), GlobalConstants.TEMPLATE_OSS_NAME_MORTGAGE_ITEM);*/
            return R.ok(ossClient.getPreviewUrl(GlobalConstants.TEMPLATE_OSS_NAME_MORTGAGE_ITEM, GlobalConstants.FILE_TEMPLATE_EXPIRY));
        } catch (Exception e) {
            log.error("下载抵押清单模板发生未知异常", e);
            throw new MithrasException("下载抵押清单模板发生未知异常");
        }
    }

}