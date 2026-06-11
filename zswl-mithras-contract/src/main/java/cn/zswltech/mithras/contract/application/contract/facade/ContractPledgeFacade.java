package cn.zswltech.mithras.contract.application.contract.facade;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.contract.application.contract.ContractPledgeApplicationService;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationRSP;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeAddREQ;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeListRSP;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeModifyREQ;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeRemoveREQ;
import cn.zswltech.mithras.contract.annotation.ContractChangeOther;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.contract.application.auth.ContractBaseAddSubAuthChecker;
import cn.zswltech.mithras.contract.application.auth.ContractBaseModifyMainAuthChecker;
import cn.zswltech.mithras.contract.application.auth.ContractBaseModifySubAuthChecker;
import cn.zswltech.mithras.contract.application.auth.ContractBaseRemoveSubAuthChecker;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.contract.mapper.contract.ContractPledgeMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.contract.core.ContractPledgeService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Service;

/**
* @description 合同-质押措施
* @author vico
* @date 2022-08-12
*/
@Slf4j
@Service
public class ContractPledgeFacade implements ContractPledgeApplicationService {

    @Resource
    private ContractPledgeService contractPledgeService;

    @Resource
    private HttpServletResponse httpServletResponse;

    @Resource
    private OssClient ossClient;

    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseAddSubAuthChecker.class, businessModule = "CONTRACT")
    public R<Boolean> add(ContractPledgeAddREQ req) {
        return R.ok(contractPledgeService.add(req));
    }

    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseModifySubAuthChecker.class, businessModule = "CONTRACT", mapperClass = ContractPledgeMapper.class)
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
    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseRemoveSubAuthChecker.class, businessModule = "CONTRACT", mapperClass = ContractPledgeMapper.class)
    public R<Void> remove(ContractPledgeRemoveREQ req){
        contractPledgeService.remove(req);
        return R.ok();
    }

    @Override
    @ContractChangeOther
    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseModifyMainAuthChecker.class, businessModule = "CONTRACT", mapperClass = ContractPledgeMapper.class)
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
