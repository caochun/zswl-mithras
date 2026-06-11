package cn.zswltech.mithras.application.orchestration.facade.policy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.policy.application.ledger.PolicyLedgerApplicationService;
import cn.zswltech.mithras.dto.policy.*;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.policy.enums.PolicyRenewInsuranceEnum;
import cn.zswltech.mithras.policy.enums.PolicyTypeEnum;
import cn.zswltech.mithras.policy.excel.exporter.PolicyLedgerContractExcelExporter;
import cn.zswltech.mithras.policy.excel.exporter.PolicyMaintenanceExcelExporter;
import cn.zswltech.mithras.policy.excel.model.PolicyLedgerContractExcelModel;
import cn.zswltech.mithras.policy.excel.model.PolicyMaintenanceExcelModel;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.policy.PolicyLedgerService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @create: 2023-06-15
 **/

@Slf4j
@Service
public class PolicyLedgerFacade implements PolicyLedgerApplicationService {

    @Resource
    private PolicyLedgerService policyLedgerService;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private PolicyLedgerContractExcelExporter policyLedgerContractExcelExporter;
    @Resource
    private PolicyMaintenanceExcelExporter policyMaintenanceExcelExporter;

    @Override
    public R<PolicyLedgerDetailRSP> detail(@Valid PolicyLedgerDetailREQ req) {

        return R.ok(policyLedgerService.detail(req));
    }

    @Override
    public R<List<PolicyLedgerRenewInsuranceRSP>> renewInsurance(@Valid PolicyLedgerRenewInsuranceREQ req) {
        return R.ok(policyLedgerService.renewInsurance(req));
    }

    @Override
    public R<PageR<PolicyLedgerListRSP>> list(@Valid PolicyLedgerListREQ req) {
        return R.ok(policyLedgerService.list(req));
    }
    @Override
    public R<Void> exportList(@Valid PolicyLedgerListExportREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("保单台账" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            policyLedgerService.export(req, httpServletResponse.getOutputStream());
            return R.ok();
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出保单台账发生未知异常", e);
            return R.fail("导出保单台账发生未知异常");
        }
    }

    @Override
    public R<List<PolicyMaintenanceRSP>> maintenanceList(@Valid PolicyMaintenanceREQ req) {
        return R.ok(policyLedgerService.maintenanceList2(req,null, null));
    }

    @Override
    public R<Void> maintenanceListExport(@Valid PolicyMaintenanceListExportREQ req) {
        try {
            List<PolicyMaintenanceRSP> policyMaintenanceRSPS = policyLedgerService.maintenanceList2(req, req.getPolicyIds(), req.getPaymentPolicyIds());

            if( ObjectUtils.isNotEmpty(policyMaintenanceRSPS)){
                httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
                httpServletResponse.setHeader("Content-Disposition",
                        "attachment;filename=" + URLEncoder.encode("待维护保单" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
                List<PolicyMaintenanceExcelModel> models = new ArrayList<>();
                policyMaintenanceRSPS.forEach(policyMaintenanceRSP -> {
                    PolicyMaintenanceExcelModel model = BeanUtil.copyProperties(policyMaintenanceRSP, PolicyMaintenanceExcelModel.class, "overdueDays", "policyAmount");
                    if(CollUtil.isNotEmpty(policyMaintenanceRSP.getProjCosponsorUserNames())) {
                        model.setProjCosponsorUserNames(String.join(",", policyMaintenanceRSP.getProjCosponsorUserNames()));
                    }
                    model.setOverdueDays(LongUtil.null2zero(policyMaintenanceRSP.getOverdueDays()));
                    model.setPolicyAmount(LongUtil.tenThousand2Dollar(LongUtil.null2zero(policyMaintenanceRSP.getPolicyAmount()).toString()));
                    model.setPolicyType(Optional.ofNullable(PolicyTypeEnum.of(policyMaintenanceRSP.getPolicyType())).map(PolicyTypeEnum::display).orElse(policyMaintenanceRSP.getPolicyType()));
                    model.setRemainingUnpaidPrincipal(LongUtil.tenThousand2Dollar(LongUtil.null2zero(policyMaintenanceRSP.getRemainingUnpaidPrincipal()).toString()).setScale(2, RoundingMode.HALF_UP));
                    models.add(model);
                });
                policyMaintenanceExcelExporter.exportExcel(models, httpServletResponse.getOutputStream());
                return R.ok();
            }
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出保单台账发生未知异常", e);
            return R.fail("导出保单台账发生未知异常");
        }
        return R.ok();
    }

    @Override
    public R<PolicyLedgerContractDetailRSP> contractDetail(@Valid PolicyLedgerDetailREQ req) {
        return R.ok(policyLedgerService.contractDetail(req));
    }

    @Override
    public R<List<PolicyInfoDetailRSP>> contractPolicy(@Valid PolicyLedgerContractPolicyREQ req) {
        List<PolicyInfoDetailRSP> policyInfoDetailRSPS = policyLedgerService.contractPolicy(req);
        List<PolicyInfoDetailRSP> zeroPolicy = null;
        if(CollectionUtil.isNotEmpty(policyInfoDetailRSPS)){
            //递归构建层级
            zeroPolicy = policyInfoDetailRSPS.stream().filter(policyInfo -> policyInfo.getLevel() == 0).collect(Collectors.toList());
            Map<Long, List<PolicyInfoDetailRSP>> policyMap = policyInfoDetailRSPS.stream().filter(policyInfo -> policyInfo.getLevel() != 0).collect(Collectors.groupingBy(PolicyInfoDetailRSP::getParentId));
            circulatePolicy(policyMap, zeroPolicy);
        }
        return R.ok(zeroPolicy);
    }
    private void circulatePolicy(Map<Long, List<PolicyInfoDetailRSP>> policyMap, List<PolicyInfoDetailRSP> policyInfos){
        if(CollectionUtil.isNotEmpty(policyInfos)){
            policyInfos.forEach(policyInfoDetailRSP -> {
                List<PolicyInfoDetailRSP> policyInfoDetailRSPS = policyMap.get(policyInfoDetailRSP.getId());
                if(CollectionUtil.isNotEmpty(policyInfoDetailRSPS)){
                    policyInfoDetailRSP.setChildren(policyInfoDetailRSPS);
                    circulatePolicy(policyMap, policyInfoDetailRSPS);
                }
            });
        }
    }

    @Override
    public R<Void> contractPolicyExport(@Valid PolicyLedgerContractPolicyREQ req) {
        try {
            List<PolicyInfoDetailRSP> policyInfoDetailRSPS = policyLedgerService.contractPolicy(req);
            if (ObjectUtils.isNotEmpty(policyInfoDetailRSPS)) {
                List<PolicyInfoDetailRSP> zeroPolicy;
                //递归构建层级
                zeroPolicy = policyInfoDetailRSPS.stream().filter(policyInfo -> policyInfo.getLevel() == 0).collect(Collectors.toList());
                Map<Long, List<PolicyInfoDetailRSP>> policyMap = policyInfoDetailRSPS.stream().filter(policyInfo -> policyInfo.getLevel() != 0).collect(Collectors.groupingBy(PolicyInfoDetailRSP::getParentId));
                circulatePolicy(policyMap, zeroPolicy);
                //循环构建
                List<PolicyLedgerContractExcelModel> policyLedgerContractExcelModels = new ArrayList<>();
                circulateBuildModel(policyLedgerContractExcelModels, zeroPolicy);
                httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
                httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("合同保单" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
                policyLedgerContractExcelExporter.exportExcel(policyLedgerContractExcelModels, httpServletResponse.getOutputStream());
            }
            return R.ok();
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出合同保单发生未知异常", e);
            return R.fail("导出合同保单发生未知异常");
        }
    }

    private void circulateBuildModel(List<PolicyLedgerContractExcelModel> policyLedgerContractExcelModels, List<PolicyInfoDetailRSP> policyInfos){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        policyInfos.forEach(rsp -> {
            PolicyLedgerContractExcelModel model = new PolicyLedgerContractExcelModel();
            model.setPolicyCode(rsp.getPolicyCode());
            model.setInsuranceCompany(rsp.getInsuranceCompany());
            PolicyTypeEnum policyInfo = PolicyTypeEnum.of(rsp.getPolicyType());
            PolicyRenewInsuranceEnum insuranceEnum = PolicyRenewInsuranceEnum.of(rsp.getRenewInsuranceFlag());
            if (ObjectUtils.isNotEmpty(policyInfo)) {
                model.setPolicyType(policyInfo.display);
            }
            if (ObjectUtils.isNotEmpty(insuranceEnum)) {
                model.setRenewInsuranceFlag(insuranceEnum.display());
            }
            model.setPolicyAmount(LongUtil.tenThousand2Dollar(LongUtil.null2zero(rsp.getPolicyAmount()).toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
            if(ObjectUtils.isNotEmpty(rsp.getInsuranceStartDate())){
                model.setInsuranceStartDate(rsp.getInsuranceStartDate().format(dateTimeFormatter));
            }
            if(ObjectUtils.isNotEmpty(rsp.getInsuranceEndDate())){
                model.setInsuranceEndDate(rsp.getInsuranceEndDate().format(dateTimeFormatter));
            }
            model.setRemark(rsp.getRemark());
            model.setIdentificationInformation(rsp.getIdentificationInformation());
            model.setCreateName(rsp.getCreateName());
            model.setCreateTime(rsp.getCreateTime().format(dateTimeFormatter));
            model.setRenewalRelationship(getPolicyLevel(rsp.getLevel()));
            policyLedgerContractExcelModels.add(model);
            if (CollectionUtil.isNotEmpty(rsp.getChildren())){
                circulateBuildModel(policyLedgerContractExcelModels, rsp.getChildren());
            }
        });
    }

    //计算保单等级-这里统一维护，方便更新
    private String getPolicyLevel(Integer level) {
        if (null == level || level <= 0) {
            return "原保单";
        } else {
            return "第" + level + "次续保";
        }
    }

    @Override
    public R<Void> sync(@Valid PolicyLedgerTmpSyncREQ req) {
        policyLedgerService.syncLedgerPolicy(req);
        return R.ok();
    }
}
