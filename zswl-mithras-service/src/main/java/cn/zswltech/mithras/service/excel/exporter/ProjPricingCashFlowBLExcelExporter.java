package cn.zswltech.mithras.service.excel.exporter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.common.constant.GlobalConstants;
import cn.zswltech.mithras.service.convert.projpricing.ProjPricingCashFlowPlanConverter;
import cn.zswltech.mithras.service.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.service.excel.exporter.AbstractCashFlowExcelExporter;
import cn.zswltech.mithras.service.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.service.excel.model.CashFlowRichExcelModel;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingCashFlowPlan;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingFactoringPrice;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingFactoringPriceLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.bo.ProjPricingCashFlowExporterBO;
import cn.zswltech.mithras.service.service.lib.projpricing.ProjPricingFactoringPriceLibService;
import cn.zswltech.mithras.service.service.lib.projpricing.handler.impl.ProjPricingFactoringPriceLibHandler;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingFactoringPriceService;
import cn.zswltech.mithras.service.util.FinancialUtil;
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
public class ProjPricingCashFlowBLExcelExporter extends AbstractCashFlowExcelExporter<ProjPricingCashFlowExporterBO> {
    @Resource
    private ProjPricingFactoringPriceService projPricingFactoringPriceService;
    @Resource
    private ProjPricingFactoringPriceLibService projPricingFactoringPriceLibService;
    @Resource
    private ProjPricingFactoringPriceLibHandler projPricingFactoringPriceLibHandler;

    @Override
    protected CashFlowRichExcelModel prepare(ProjPricingCashFlowExporterBO projPricingCashFlowExporterBO) {
        CashFlowRichExcelModel cashFlowRichExcelModel = this.assemblyBL(projPricingCashFlowExporterBO);
        // 现金流计划表
        List<ProjPricingCashFlowPlan> projPricingCashFlowPlanList = projPricingCashFlowExporterBO.getProjPricingCashFlowPlanList();
        if (CollectionUtil.isNotEmpty(projPricingCashFlowPlanList)) {
            List<CashFlowExcelModel> cashFlowExcelModelList = projPricingCashFlowPlanList.stream().map(ProjPricingCashFlowPlanConverter::toCashFlowExcelModel).collect(Collectors.toList());
            cashFlowRichExcelModel.setCashFlowList(new LinkedList<>(cashFlowExcelModelList));
            cashFlowRichExcelModel.getCashFlowList().addFirst(this.getZeroPhaseCashFlow(cashFlowRichExcelModel, null));
        }
        // 特殊字段指定
        cashFlowRichExcelModel.setMoneyCellName("应收保理款（元）");
        return cashFlowRichExcelModel;
    }

    private CashFlowRichExcelModel assemblyBL(ProjPricingCashFlowExporterBO projPricingCashFlowExporterBO) {
        ProjPricingBaseInfo projPricingBaseInfo = projPricingCashFlowExporterBO.getProjPricingBaseInfo();
        ProjPricingFactoringPrice projPricingFactoringPrice;
        if (StrUtil.isBlank(projPricingCashFlowExporterBO.getVersion())) {
            projPricingFactoringPrice = projPricingFactoringPriceService.getByProjectId(projPricingBaseInfo.getId());
            Assert.notNull(projPricingFactoringPrice, () -> MithrasException.newException("请先保存报价方案"));
        } else {
            ProjPricingFactoringPriceLib projPricingFactoringPriceLib = projPricingFactoringPriceLibService.getByProjPricingIdAndVersion(projPricingBaseInfo.getId(), projPricingCashFlowExporterBO.getVersion());
            Assert.notNull(projPricingFactoringPriceLib, () -> MithrasException.newException("对应版本的报价方案数据不存在"));
            projPricingFactoringPrice = projPricingFactoringPriceLibHandler.actualLib2Entity(projPricingFactoringPriceLib);
        }
        CashFlowRichExcelModel cashFlowRichExcelModel = new CashFlowRichExcelModel();
        cashFlowRichExcelModel.setBizType(projPricingBaseInfo.getBizType());
        cashFlowRichExcelModel.setMonthCount(projPricingFactoringPrice.getFactoringCreditTerm());
        if (StrUtil.isNotBlank(projPricingFactoringPrice.getRepayRate())) {
            Integer repayTimes = FinancialUtil.getRepayTimesInYear(projPricingFactoringPrice.getRepayRate());
            if (Objects.nonNull(repayTimes)) {
                cashFlowRichExcelModel.setRepayTimesInYear(repayTimes);
            }
        }
        cashFlowRichExcelModel.setRentCalculateWay(Optional.ofNullable(RepayCalcType.find(projPricingFactoringPrice.getRentalCalcType())).map(RepayCalcType::display).orElse(""));
        if (Objects.nonNull(projPricingFactoringPrice.getFactoringRatePercent())) {
            BigDecimal temp = NumberUtil.div(projPricingFactoringPrice.getFactoringRatePercent().toString(), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setInterestRate(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        if (Objects.nonNull(projPricingFactoringPrice.getIrrPercent())) {
            BigDecimal temp = NumberUtil.div(projPricingFactoringPrice.getIrrPercent().toString(), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setIrr(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        cashFlowRichExcelModel.setProjectAmount(NumberUtil.div(projPricingFactoringPrice.getApplyCreditAmount().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setConsultingFee(NumberUtil.div(projPricingFactoringPrice.getConsultingFee().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setDownPayment(BigDecimal.valueOf(0).setScale(2, RoundingMode.UP));
        cashFlowRichExcelModel.setEarnest(NumberUtil.div(projPricingFactoringPrice.getEarnestMoney().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setNominalPrice(BigDecimal.valueOf(0).setScale(2, RoundingMode.UP));
        return cashFlowRichExcelModel;
    }
}
