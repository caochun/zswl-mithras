package cn.zswltech.mithras.service.controller.creditreport;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.creditreport.CreditReportUnsettledSummaryApi;
import cn.zswltech.mithras.dto.creditreport.CreditReportUnsettledSummaryListREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportUnsettledSummaryListRSP;
import cn.zswltech.mithras.dto.creditreport.CreditReportUnsettledSummaryModifyREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportUnsettledSummaryRemoveREQ;
import cn.zswltech.mithras.service.enums.creditreport.CreditReportBusinessTypeEnum;
import cn.zswltech.mithras.service.enums.creditreport.CreditReportPaymentGuaranteeModuleEnum;
import cn.zswltech.mithras.service.enums.creditreport.CreditReportPaymentModuleEnum;
import cn.zswltech.mithras.service.mapper.model.creditreport.CreditReportUnsettledSummary;
import cn.zswltech.mithras.service.service.creditreport.CreditReportUnsettledSummaryService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description 征信报告-未结清信贷及授信信息表
* @author vico
* @date 2025-11-14
*/
@RestController
public class CreditReportUnsettledSummaryController implements CreditReportUnsettledSummaryApi {

    @Resource
    private CreditReportUnsettledSummaryService creditReportUnsettledSummaryService;


    @Override
    public R<Void> modify(CreditReportUnsettledSummaryModifyREQ req){
        creditReportUnsettledSummaryService.modify(req);
        return R.ok();
    }

    @Override
    public R<List<CreditReportUnsettledSummaryListRSP>> list(CreditReportUnsettledSummaryListREQ req){
        List<CreditReportUnsettledSummary> data = creditReportUnsettledSummaryService.list(req);
        //分组
        if (ObjectUtil.isEmpty(data)) {
            return R.ok();
        }
        Map<String, List<CreditReportUnsettledSummary>> module2List = data.stream().collect(Collectors.groupingBy(CreditReportUnsettledSummary::getPaymentModule));
        List<CreditReportUnsettledSummaryListRSP> rsps = new ArrayList<>();
        List<String> needCounts = CreditReportBusinessTypeEnum.needCount();
        module2List.forEach((k, v) -> {
            if(ObjectUtil.isNotEmpty(v)) {
                CreditReportUnsettledSummaryListRSP rsp = new CreditReportUnsettledSummaryListRSP();
                rsp.setPaymentModule(k);
                List<CreditReportUnsettledSummaryListRSP.UnsettledSummaryBody> unsettledSummaryBodies = BeanUtil.copyToList(v, CreditReportUnsettledSummaryListRSP.UnsettledSummaryBody.class);
                rsp.setBodyList(unsettledSummaryBodies);
                if (needCounts.contains(k)){
                    unsettledSummaryBodies.forEach(e -> e.setPaymentTypeName(Optional.ofNullable(CreditReportPaymentGuaranteeModuleEnum.finaByName(e.getPaymentType())).map(CreditReportPaymentGuaranteeModuleEnum::display).orElse(e.getPaymentType())));
                    rsp.setPaymentModuleName(Optional.ofNullable(CreditReportBusinessTypeEnum.finaByName(k)).map(CreditReportBusinessTypeEnum::display).orElse(k));
                    //添加合计
                    addCount(unsettledSummaryBodies);
                } else {
                    unsettledSummaryBodies.forEach(e -> e.setPaymentTypeName(Optional.ofNullable(CreditReportPaymentModuleEnum.finaByName(e.getPaymentType())).map(CreditReportPaymentModuleEnum::display).orElse(e.getPaymentType())));
                }
                rsps.add(rsp);
            }

        });
        return R.ok(rsps);
    }

    private void addCount(List<CreditReportUnsettledSummaryListRSP.UnsettledSummaryBody> bodyList) {
        Map<String, CreditReportUnsettledSummaryListRSP.UnsettledSummaryBody> fundClassification2Bean = new HashMap<>();
        bodyList.forEach(e -> {
            CreditReportUnsettledSummaryListRSP.UnsettledSummaryBody body = fundClassification2Bean.get(e.getFundClassification());
            if (ObjectUtil.isEmpty(body)) {
                body = new CreditReportUnsettledSummaryListRSP.UnsettledSummaryBody();
                body.setAccountNumber(0);
                body.setAccountAmount(BigDecimal.ZERO);
            }
            body.setPaymentType(CreditReportPaymentGuaranteeModuleEnum.TOTAL.name());
            body.setPaymentTypeName(CreditReportPaymentGuaranteeModuleEnum.TOTAL.display());
            body.setAccountNumber(body.getAccountNumber() + e.getAccountNumber());
            body.setAccountAmount(body.getAccountAmount().add(e.getAccountAmount() == null ? BigDecimal.ZERO : e.getAccountAmount()));
            fundClassification2Bean.put(e.getFundClassification(), body);
        });
        bodyList.addAll(fundClassification2Bean.values());
    }

    @Override
    public R<Void> remove(CreditReportUnsettledSummaryRemoveREQ req){
        creditReportUnsettledSummaryService.remove(req);
        return R.ok();
    }

}