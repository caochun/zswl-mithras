package cn.zswltech.mithras.service.controller.contract;

import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.contract.ContractLeaseItemApi;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.leaseitem.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.contract.ContractLeaseItemAuthChecker;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.leaseholdproperty.LeaseItemInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractLeaseItemService;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseItemInfoService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/8/16
 * @description
 */
@Slf4j
@RestController
public class ContractLeaseItemController implements ContractLeaseItemApi {
    @Resource
    private ContractLeaseItemService contractLeaseItemService;
    @Resource
    private OssClient ossClient;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private LeaseItemInfoService leaseItemInfoService;

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractLeaseItemAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT)
    @Override
    public R<Void> importExcel(ContractLeaseItemImportREQ contractLeaseItemImportREQ) {
        try {
            contractLeaseItemService.importExcel(contractLeaseItemImportREQ.getFile().getInputStream(), contractLeaseItemImportREQ.getContractId());
            return R.ok();
        } catch (IOException e) {
            log.error("租赁物清单表读取异常", e);
            throw new MithrasException("读取导入文件异常");
        }
    }

    @Override
    public R<ContractLeaseItemListRSP> listLeaseItem(ContractLeaseItemListREQ req) {
        return R.ok(contractLeaseItemService.pageList(req));
    }

    @Override
    public R<String> downloadTemplate() {
        try {
            /*httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(GlobalConstants.TEMPLATE_OSS_NAME_CONTRACT_LEASE_ITEM, StandardCharsets.UTF_8.name()));
            ossClient.downLoad(httpServletResponse.getOutputStream(), GlobalConstants.TEMPLATE_OSS_NAME_CONTRACT_LEASE_ITEM);*/
            return R.ok(ossClient.getPreviewUrl(GlobalConstants.TEMPLATE_OSS_NAME_CONTRACT_LEASE_ITEM, GlobalConstants.FILE_TEMPLATE_EXPIRY));
        } catch (Exception e) {
            log.error("下载租赁物清单模板发生未知异常", e);
            throw new MithrasException("下载租赁物清单模板发生未知异常");
        }
    }

    @Override
    public R<ContractPreChooseLeaseItemRSP> getPreChooseLeaseItem(@Valid ContractPreChooseLeaseItemREQ req) {
        return R.ok(contractLeaseItemService.getPreChooseLeaseItem(req));
    }

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractLeaseItemAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT)
    @Override
    public R<Void> chooseLeaseItem(@Valid ContractChooseLeaseItemREQ req) {
        contractLeaseItemService.chooseLeaseItem(req);
        return R.ok();
    }

    @Override
    public void exportExcel(@Valid ContractLeaseItemExportREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode("租赁物清单" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            contractLeaseItemService.exportExcel(httpServletResponse.getOutputStream(), req);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("下载租赁物清单发生未知异常", e);
            throw new MithrasException("下载租赁物清单发生未知异常");
        }
    }

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractLeaseItemAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT)
    @Override
    public R<Void> saveLeaseItemTotalAmount(@Valid ContractLeaseItemTotalAmountREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同信息不存在");
        }
//        if (req.getLeaseItemTotalAmount() * 0.9 < contractBaseInfo.getApplyCreditAmount()) {
//            throw new MithrasException("租赁物总额*90%小于合同金额，不允许保存");
//        }
//        contractBaseInfo.setItemTotalAmount(req.getLeaseItemTotalAmount());
        contractBaseInfoService.updateById(contractBaseInfo);
        return R.ok();
    }

    @Override
    public R<Void> checkLeaseItemInProcess(@Valid ContractSingleIdREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同信息不存在");
        }
        LambdaQueryWrapper<LeaseItemInfo> query = Wrappers.lambdaQuery();
        query.eq(LeaseItemInfo::getProjReviewId, contractBaseInfo.getProjReviewId());
        query.orderByDesc(LeaseItemInfo::getId);
        query.last(StringUtil.mysqlLimitOne());
        LeaseItemInfo leaseItemInfo = leaseItemInfoService.getOne(query);
        if (Objects.nonNull(leaseItemInfo) && Objects.equals(leaseItemInfo.getApprovalStatus(), ProcessStatus.UNDER_APPROVAL.name())) {
            throw new MithrasException("存在审批中的租赁物，不允许执行该操作");
        }
        return R.ok();
    }
}
