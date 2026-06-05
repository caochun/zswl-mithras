package cn.zswltech.mithras.service.controller.policy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.dto.PaymentPoliceTmpImportREQ;
import cn.zswltech.mithras.api.policy.PolicyInfoTmpApi;
import cn.zswltech.mithras.dto.policy.*;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.kpi.enums.config.TaxRateEnum;
import cn.zswltech.mithras.policy.domain.enums.PolicyRenewInsuranceEnum;
import cn.zswltech.mithras.policy.domain.enums.PolicyTypeEnum;
import cn.zswltech.mithras.policy.excel.exporter.PaymentPolicyExcelExporter;
import cn.zswltech.mithras.policy.excel.model.PaymentPolicyExcelModel;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.policy.infrastructure.persistence.model.PolicyInfo;
import cn.zswltech.mithras.policy.infrastructure.persistence.model.PolicyInfoTmp;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.policy.PolicyInfoTmpService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
* @description 保单暂存表c
* @author vico
* @date 2023-10-23
*/
@RestController
@Slf4j
public class PolicyInfoTmpController implements PolicyInfoTmpApi {

    @Resource
    private PolicyInfoTmpService policyInfoTmpService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private PaymentPolicyExcelExporter paymentPolicyExcelExporter;

    @Override
    public R<Long> add(PolicyInfoTmpAddREQ req) {
        return R.ok(policyInfoTmpService.add(req));
    }

    @Override
    public R<Void> modify(PolicyInfoTmpModifyREQ req){
        policyInfoTmpService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<PolicyInfoTmpListRSP>> list(PolicyInfoTmpListREQ req){
        Page<PolicyInfoTmp> data = policyInfoTmpService.list(req);
        List<PolicyInfoTmpListRSP> list = BeanUtil.copyToList(data.getRecords(), PolicyInfoTmpListRSP.class);
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(list.stream().map(PolicyInfoTmpListRSP::getCreateBy).collect(Collectors.toSet()));
        list.forEach(base -> {
            base.setCreateName(userId2Name.get(base.getCreateBy()));
        });
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    // 废弃
//    @Override
//    public R<PolicyInfoTmpListRSP> get(PolicyInfoTmpDetailREQ req) {
//        PolicyInfoTmp policyInfoTmp = policyInfoTmpService.getOne(Wrappers.<PolicyInfoTmp>lambdaQuery()
//                .eq(req.getParentPolicyId() != null, PolicyInfoTmp::getParentPolicyId, req.getParentPolicyId())
//                .eq(PolicyInfoTmp::getCreateBy, AccountUtil.getLoginInfo().getId()));
//        PolicyInfoTmpListRSP rsp = null;
//        if(policyInfoTmp != null) {
//            List<PolicyInfoMaterialsListRSP> files = null;
//            rsp = BeanUtil.copyProperties(policyInfoTmp, PolicyInfoTmpListRSP.class);
//            rsp.setIsTemp(true);
//            List<MaterialsList> materialsLists = materialsListService.list(Wrappers.<MaterialsList>lambdaQuery().eq(MaterialsList::getBusinessType, BusinessModuleEnum.POLICY_TMP.name())
//                    .eq(MaterialsList::getMaterialsType, BusinessModuleEnum.POLICY_TMP.name())
//                    .eq(ObjectUtil.isNotEmpty(rsp.getId()), MaterialsList::getBelongId, rsp.getId()));
//            if (CollUtil.isNotEmpty(materialsLists)) {
//                files = materialsLists.stream().map(m -> {
//                    PolicyInfoMaterialsListRSP tmp = new PolicyInfoMaterialsListRSP();
//                    tmp.setId(m.getId());
//                    tmp.setName(m.getFilename());
//                    return tmp;
//                }).collect(Collectors.toList());
//            }
//            rsp.setFiles(files);
//        }
//        return R.ok(rsp);
//    }

    @Override
    public R<Void> remove(PolicyInfoTmpRemoveREQ req){
        policyInfoTmpService.removeByIds(req.getIds());
        return R.ok();
    }

    @Override
    public R<String> importExcel(@Valid PaymentPoliceTmpImportREQ paymentPoliceImportREQ) {
        try {
            return R.ok(policyInfoTmpService.importExcel(paymentPoliceImportREQ.getFile().getInputStream(), paymentPoliceImportREQ.getContractId()));
        } catch (IOException e) {
            throw new MithrasException("导入保单信息错误");
        }
    }

    @Override
    public R<Void> policyTmpExport(@Valid PolicyTmpExportREQ req) {
        try {
            List<PolicyInfoTmp> policyInfoTmps = policyInfoTmpService.list(Wrappers.<PolicyInfoTmp>lambdaQuery()
            .in(CollectionUtil.isNotEmpty(req.getIds()), PolicyInfoTmp::getId, req.getIds()));
            if (ObjectUtils.isNotEmpty(policyInfoTmps)) {
                Map<Long, String> userId2Name = id2NameService.sysUserId2Name(policyInfoTmps.stream().map(PolicyInfoTmp::getCreateBy).collect(Collectors.toSet()));
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                List<PaymentPolicyExcelModel> policyLedgerContractExcelModels = new ArrayList<>();
                policyInfoTmps.forEach(rsp -> {
                    PaymentPolicyExcelModel model = new PaymentPolicyExcelModel();
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
                    model.setPolicyAmount(LongUtil.tenThousand2Dollar(LongUtil.null2zero(rsp.getPolicyAmount()).toString()).setScale(2, BigDecimal.ROUND_HALF_UP));
                    if(ObjectUtils.isNotEmpty(rsp.getInsuranceStartDate())){
                        model.setInsuranceStartDate(rsp.getInsuranceStartDate().format(dateTimeFormatter));
                    }
                    if(ObjectUtils.isNotEmpty(rsp.getInsuranceEndDate())){
                        model.setInsuranceEndDate(rsp.getInsuranceEndDate().format(dateTimeFormatter));
                    }
                    model.setRemark(rsp.getRemark());
                    model.setIdentificationInformation(rsp.getIdentificationInformation());
                    model.setCreateByName(userId2Name.get(rsp.getCreateBy()));
                    model.setCreateTime(rsp.getCreateTime().toLocalDate());
                    policyLedgerContractExcelModels.add(model);
                });
                httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
                httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("保单" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
                paymentPolicyExcelExporter.exportExcel(policyLedgerContractExcelModels, httpServletResponse.getOutputStream());
            }
            return R.ok();
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出保单发生未知异常", e);
            return R.fail("导出保单发生未知异常");
        }
    }

}