package cn.zswltech.mithras.service.excel.exporter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.convert.projreview.ProjReviewCashFlowPlanConverter;
import cn.zswltech.mithras.service.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.service.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.service.excel.model.CashFlowRichExcelModel;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewAocPrice;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewAocPriceLib;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewCashFlowPlan;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.bo.ProjReviewCashFlowExporterBO;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewAocPriceLibService;
import cn.zswltech.mithras.service.service.lib.projreview.handler.impl.ProjReviewAocPriceLibHandler;
import cn.zswltech.mithras.service.service.projreview.ProjReviewAocPriceService;
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
public class ProjReviewCashFlowZRExcelExporter extends AbstractCashFlowExcelExporter<ProjReviewCashFlowExporterBO> {
    @Resource
    private ProjReviewAocPriceService projReviewAocPriceService;
    @Resource
    private ProjReviewAocPriceLibService projReviewAocPriceLibService;
    @Resource
    private ProjReviewAocPriceLibHandler projReviewAocPriceLibHandler;

    @Override
    protected CashFlowRichExcelModel prepare(ProjReviewCashFlowExporterBO projReviewCashFlowExporterBO) {
        CashFlowRichExcelModel cashFlowRichExcelModel = this.assemblyZR(projReviewCashFlowExporterBO);
        // 现金流计划表
        List<ProjReviewCashFlowPlan> projReviewCashFlowPlanList = projReviewCashFlowExporterBO.getProjReviewCashFlowPlanList();
        if (CollectionUtil.isNotEmpty(projReviewCashFlowPlanList)) {
            List<CashFlowExcelModel> cashFlowExcelModelList = projReviewCashFlowPlanList.stream().map(ProjReviewCashFlowPlanConverter::toCashFlowExcelModel).collect(Collectors.toList());
            cashFlowRichExcelModel.setCashFlowList(new LinkedList<>(cashFlowExcelModelList));
            cashFlowRichExcelModel.getCashFlowList().addFirst(this.getZeroPhaseCashFlow(cashFlowRichExcelModel, null));
        }
        // 特殊字段表头
        cashFlowRichExcelModel.setMoneyCellName("回收款（元）");
        return cashFlowRichExcelModel;
    }

    private CashFlowRichExcelModel assemblyZR(ProjReviewCashFlowExporterBO projReviewCashFlowExporterBO) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewCashFlowExporterBO.getProjReviewBaseInfo();
        ProjReviewAocPrice projReviewAocPrice;
        if (StrUtil.isBlank(projReviewCashFlowExporterBO.getVersion())) {
            projReviewAocPrice = projReviewAocPriceService.getByProjectId(projReviewBaseInfo.getId());
            Assert.notNull(projReviewAocPrice, () -> MithrasException.newException("请先保存报价方案"));
        } else {
            ProjReviewAocPriceLib projReviewAocPriceLib = projReviewAocPriceLibService.getByProjReviewIdAndVersion(projReviewBaseInfo.getId(), projReviewCashFlowExporterBO.getVersion());
            Assert.notNull(projReviewAocPriceLib, () -> MithrasException.newException("对应版本的报价方案数据不存在"));
            projReviewAocPrice = projReviewAocPriceLibHandler.actualLib2Entity(projReviewAocPriceLib);
        }
        CashFlowRichExcelModel cashFlowRichExcelModel = new CashFlowRichExcelModel();
        cashFlowRichExcelModel.setBizType(projReviewBaseInfo.getBizType());
        cashFlowRichExcelModel.setMonthCount(projReviewAocPrice.getAocCreditTerm());
        if (StrUtil.isNotBlank(projReviewAocPrice.getRepayRate())) {
            Integer repayTimes = FinancialUtil.getRepayTimesInYear(projReviewAocPrice.getRepayRate());
            if (Objects.nonNull(repayTimes)) {
                cashFlowRichExcelModel.setRepayTimesInYear(repayTimes);
            }
        }
        cashFlowRichExcelModel.setStartRentDate(projReviewAocPrice.getPlannedStartingDate());
        cashFlowRichExcelModel.setRentCalculateWay(Optional.ofNullable(RepayCalcType.find(projReviewAocPrice.getRentalCalcType())).map(RepayCalcType::display).orElse(""));
        if (Objects.nonNull(projReviewAocPrice.getAocRatePercent())) {
            BigDecimal temp = NumberUtil.div(projReviewAocPrice.getAocRatePercent().toString(), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setInterestRate(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        if (Objects.nonNull(projReviewAocPrice.getIrrPercent())) {
            BigDecimal temp = NumberUtil.div(projReviewAocPrice.getIrrPercent().toString(), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setIrr(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        cashFlowRichExcelModel.setProjectAmount(NumberUtil.div(projReviewAocPrice.getApplyCreditAmount().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setConsultingFee(NumberUtil.div(projReviewAocPrice.getConsultingFee().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setDownPayment(BigDecimal.valueOf(0).setScale(2, RoundingMode.UP));
        cashFlowRichExcelModel.setEarnest(NumberUtil.div(projReviewAocPrice.getEarnestMoney().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setNominalPrice(BigDecimal.valueOf(0).setScale(2, RoundingMode.UP));
        return cashFlowRichExcelModel;
    }
}
