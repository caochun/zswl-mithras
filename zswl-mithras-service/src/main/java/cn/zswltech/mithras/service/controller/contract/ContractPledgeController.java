package cn.zswltech.mithras.service.controller.contract;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.contract.ContractPledgeApi;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationRSP;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeAddREQ;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeListRSP;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeModifyREQ;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeRemoveREQ;
import cn.zswltech.mithras.service.annotation.ContractChangeOther;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseAddSubAuthChecker;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseModifyMainAuthChecker;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseModifySubAuthChecker;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseRemoveSubAuthChecker;
import cn.zswltech.mithras.common.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.contract.ContractPledgeMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractPledgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
* @description 合同-质押措施
* @author vico
* @date 2022-08-12
*/
@Slf4j
@RestController
public class ContractPledgeController implements ContractPledgeApi {

    @Resource
    private ContractPledgeService contractPledgeService;

    @Resource
    private HttpServletResponse httpServletResponse;

    @Resource
    private OssClient ossClient;

    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseAddSubAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT)
    public R<Boolean> add(ContractPledgeAddREQ req) {
        return R.ok(contractPledgeService.add(req));
    }

    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseModifySubAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT, mapperClass = ContractPledgeMapper.class)
    public R<Void> modify(ContractPledgeModifyREQ req){
        contractPledgeService.modify(req);
        return R.ok();
    }

    @Override
    public R<List<ContractPledgeListRSP>> list(ContractIdListREQ req){
        return R.ok(contractPledgeService.list(req));
    }

    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseRemoveSubAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT, mapperClass = ContractPledgeMapper.class)
    public R<Void> remove(ContractPledgeRemoveREQ req){
        contractPledgeService.remove(req);
        return R.ok();
    }

    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseModifyMainAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT, mapperClass = ContractPledgeMapper.class)
    public R<Void> generatePledgeContractCode(@Valid ContractSingleIdREQ contractSingleIdREQ) {
        contractPledgeService.generatePledgeContractCode(contractSingleIdREQ.getContractId());
        return R.ok();
    }

    @Override
    public R<String> downloadPledgeItemTemplate() {
        try {
            /*httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(GlobalConstants.TEMPLATE_OSS_NAME_PLEDGE_ITEM, StandardCharsets.UTF_8.name()));
            ossClient.downLoad(httpServletResponse.getOutputStream(), GlobalConstants.TEMPLATE_OSS_NAME_PLEDGE_ITEM);*/
            return R.ok(ossClient.getPreviewUrl(GlobalConstants.TEMPLATE_OSS_NAME_PLEDGE_ITEM, GlobalConstants.FILE_TEMPLATE_EXPIRY));
        } catch (Exception e) {
            log.error("下载质押物清单模板发生未知异常", e);
            throw new MithrasException("下载质押物清单模板发生未知异常");
        }
    }

    @Override
    public R<List<ContractRelationRSP>> relation(ContractRelationREQ req) {
        return R.ok(contractPledgeService.contractByclient(req));
    }

}