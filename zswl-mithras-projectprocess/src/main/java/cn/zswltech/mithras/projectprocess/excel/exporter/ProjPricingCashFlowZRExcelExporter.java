package cn.zswltech.mithras.projectprocess.excel.exporter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.projectprocess.convert.projpricing.ProjPricingCashFlowPlanConverter;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.projectprocess.excel.exporter.AbstractCashFlowExcelExporter;
import cn.zswltech.mithras.projectprocess.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.projectprocess.excel.model.CashFlowRichExcelModel;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingAocPrice;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingAocPriceLib;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingCashFlowPlan;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.projectprocess.application.bo.ProjPricingCashFlowExporterBO;
import cn.zswltech.mithras.projectprocess.versioning.projpricing.ProjPricingAocPriceLibService;
import cn.zswltech.mithras.projectprocess.versioning.projpricing.handler.impl.ProjPricingAocPriceLibHandler;
import cn.zswltech.mithras.projectprocess.application.projpricing.ProjPricingAocPriceService;
import cn.zswltech.mithras.foundation.excel.CashFlowExportUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/10/31
 * @description
 */
@Component
public class ProjPricingCashFlowZRExcelExporter extends AbstractCashFlowExcelExporter<ProjPricingCashFlowExporterBO> {
    @Resource
    private ProjPricingAocPriceService projPricingAocPriceService;
    @Resource
    private ProjPricingAocPriceLibService projPricingAocPriceLibService;
    @Resource
    private ProjPricingAocPriceLibHandler projPricingAocPriceLibHandler;

    @Override
    protected CashFlowRichExcelModel prepare(ProjPricingCashFlowExporterBO projPricingCashFlowExporterBO) {
        CashFlowRichExcelModel cashFlowRichExcelModel = this.assemblyZR(projPricingCashFlowExporterBO);
        // 现金流计划表
        List<ProjPricingCashFlowPlan> projPricingCashFlowPlanList = projPricingCashFlowExporterBO.getProjPricingCashFlowPlanList();
        if (CollectionUtil.isNotEmpty(projPricingCashFlowPlanList)) {
            List<CashFlowExcelModel> cashFlowExcelModelList = projPricingCashFlowPlanList.stream().map(ProjPricingCashFlowPlanConverter::toCashFlowExcelModel).collect(Collectors.toList());
            cashFlowRichExcelModel.setCashFlowList(new LinkedList<>(cashFlowExcelModelList));
            cashFlowRichExcelModel.getCashFlowList().addFirst(this.getZeroPhaseCashFlow(cashFlowRichExcelModel, null));
        }
        // 特殊字段表头
        cashFlowRichExcelModel.setMoneyCellName("回收款（元）");
        return cashFlowRichExcelModel;
    }

    private CashFlowRichExcelModel assemblyZR(ProjPricingCashFlowExporterBO projPricingCashFlowExporterBO) {
        ProjPricingBaseInfo projPricingBaseInfo = projPricingCashFlowExporterBO.getProjPricingBaseInfo();
        ProjPricingAocPrice projPricingAocPrice;
        if (StrUtil.isBlank(projPricingCashFlowExporterBO.getVersion())) {
            projPricingAocPrice = projPricingAocPriceService.getByProjectId(projPricingBaseInfo.getId());
            Assert.notNull(projPricingAocPrice, () -> MithrasException.newException("请先保存报价方案"));
        } else {
            ProjPricingAocPriceLib projPricingAocPriceLib = projPricingAocPriceLibService.getByProjPricingIdAndVersion(projPricingBaseInfo.getId(), projPricingCashFlowExporterBO.getVersion());
            Assert.notNull(projPricingAocPriceLib, () -> MithrasException.newException("对应版本的报价方案数据不存在"));
            projPricingAocPrice = projPricingAocPriceLibHandler.actualLib2Entity(projPricingAocPriceLib);
        }
        CashFlowRichExcelModel cashFlowRichExcelModel = new CashFlowRichExcelModel();
        cashFlowRichExcelModel.setBizType(projPricingBaseInfo.getBizType());
        cashFlowRichExcelModel.setMonthCount(projPricingAocPrice.getAocCreditTerm());
        if (StrUtil.isNotBlank(projPricingAocPrice.getRepayRate())) {
            Integer repayTimes = CashFlowExportUtil.getRepayTimesInYear(projPricingAocPrice.getRepayRate());
            if (Objects.nonNull(repayTimes)) {
                cashFlowRichExcelModel.setRepayTimesInYear(repayTimes);
            }
        }
        cashFlowRichExcelModel.setStartRentDate(projPricingAocPrice.getPlannedStartingDate());
        cashFlowRichExcelModel.setRentCalculateWay(Optional.ofNullable(RepayCalcType.find(projPricingAocPrice.getRentalCalcType())).map(RepayCalcType::display).orElse(""));
        if (Objects.nonNull(projPricingAocPrice.getAocRatePercent())) {
            BigDecimal temp = NumberUtil.div(projPricingAocPrice.getAocRatePercent().toString(), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setInterestRate(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        if (Objects.nonNull(projPricingAocPrice.getIrrPercent())) {
            BigDecimal temp = NumberUtil.div(projPricingAocPrice.getIrrPercent().toString(), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setIrr(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        cashFlowRichExcelModel.setProjectAmount(NumberUtil.div(projPricingAocPrice.getApplyCreditAmount().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setConsultingFee(NumberUtil.div(projPricingAocPrice.getConsultingFee().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setDownPayment(BigDecimal.valueOf(0).setScale(2, RoundingMode.UP));
        cashFlowRichExcelModel.setEarnest(NumberUtil.div(projPricingAocPrice.getEarnestMoney().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setNominalPrice(BigDecimal.valueOf(0).setScale(2, RoundingMode.UP));
        return cashFlowRichExcelModel;
    }
}
