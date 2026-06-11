package cn.zswltech.mithras.projectprocess.excel.exporter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.projectprocess.convert.projreview.ProjReviewCashFlowPlanConverter;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.projectprocess.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.projectprocess.excel.model.CashFlowRichExcelModel;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewCashFlowPlan;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewFactoringPrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewFactoringPriceLib;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.projectprocess.application.bo.ProjReviewCashFlowExporterBO;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.ProjReviewFactoringPriceLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.handler.impl.ProjReviewFactoringPriceLibHandler;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewFactoringPriceService;
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
public class ProjReviewCashFlowBLExcelExporter extends AbstractCashFlowExcelExporter<ProjReviewCashFlowExporterBO> {
    @Resource
    private ProjReviewFactoringPriceService projReviewFactoringPriceService;
    @Resource
    private ProjReviewFactoringPriceLibService projReviewFactoringPriceLibService;
    @Resource
    private ProjReviewFactoringPriceLibHandler projReviewFactoringPriceLibHandler;

    @Override
    protected CashFlowRichExcelModel prepare(ProjReviewCashFlowExporterBO projReviewCashFlowExporterBO) {
        CashFlowRichExcelModel cashFlowRichExcelModel = this.assemblyBL(projReviewCashFlowExporterBO);
        // 现金流计划表
        List<ProjReviewCashFlowPlan> projReviewCashFlowPlanList = projReviewCashFlowExporterBO.getProjReviewCashFlowPlanList();
        if (CollectionUtil.isNotEmpty(projReviewCashFlowPlanList)) {
            List<CashFlowExcelModel> cashFlowExcelModelList = projReviewCashFlowPlanList.stream().map(ProjReviewCashFlowPlanConverter::toCashFlowExcelModel).collect(Collectors.toList());
            cashFlowRichExcelModel.setCashFlowList(new LinkedList<>(cashFlowExcelModelList));
            cashFlowRichExcelModel.getCashFlowList().addFirst(this.getZeroPhaseCashFlow(cashFlowRichExcelModel, null));
        }
        // 特殊字段指定
        cashFlowRichExcelModel.setMoneyCellName("应收保理款（元）");
        return cashFlowRichExcelModel;
    }

    private CashFlowRichExcelModel assemblyBL(ProjReviewCashFlowExporterBO projReviewCashFlowExporterBO) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewCashFlowExporterBO.getProjReviewBaseInfo();
        ProjReviewFactoringPrice projReviewFactoringPrice;
        if (StrUtil.isBlank(projReviewCashFlowExporterBO.getVersion())) {
            projReviewFactoringPrice = projReviewFactoringPriceService.getByProjectId(projReviewBaseInfo.getId());
            Assert.notNull(projReviewFactoringPrice, () -> MithrasException.newException("请先保存报价方案"));
        } else {
            ProjReviewFactoringPriceLib projReviewFactoringPriceLib = projReviewFactoringPriceLibService.getByProjReviewIdAndVersion(projReviewBaseInfo.getId(), projReviewCashFlowExporterBO.getVersion());
            Assert.notNull(projReviewFactoringPriceLib, () -> MithrasException.newException("对应版本的报价方案数据不存在"));
            projReviewFactoringPrice = projReviewFactoringPriceLibHandler.actualLib2Entity(projReviewFactoringPriceLib);
        }
        CashFlowRichExcelModel cashFlowRichExcelModel = new CashFlowRichExcelModel();
        cashFlowRichExcelModel.setBizType(projReviewBaseInfo.getBizType());
        cashFlowRichExcelModel.setMonthCount(projReviewFactoringPrice.getFactoringCreditTerm());
        if (StrUtil.isNotBlank(projReviewFactoringPrice.getRepayRate())) {
            Integer repayTimes = CashFlowExportUtil.getRepayTimesInYear(projReviewFactoringPrice.getRepayRate());
            if (Objects.nonNull(repayTimes)) {
                cashFlowRichExcelModel.setRepayTimesInYear(repayTimes);
            }
        }
        cashFlowRichExcelModel.setRentCalculateWay(Optional.ofNullable(RepayCalcType.find(projReviewFactoringPrice.getRentalCalcType())).map(RepayCalcType::display).orElse(""));
        if (Objects.nonNull(projReviewFactoringPrice.getFactoringRatePercent())) {
            BigDecimal temp = NumberUtil.div(projReviewFactoringPrice.getFactoringRatePercent().toString(), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setInterestRate(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        if (Objects.nonNull(projReviewFactoringPrice.getIrrPercent())) {
            BigDecimal temp = NumberUtil.div(projReviewFactoringPrice.getIrrPercent().toString(), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setIrr(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        cashFlowRichExcelModel.setProjectAmount(NumberUtil.div(projReviewFactoringPrice.getApplyCreditAmount().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setConsultingFee(NumberUtil.div(projReviewFactoringPrice.getConsultingFee().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setDownPayment(BigDecimal.valueOf(0).setScale(2, RoundingMode.UP));
        cashFlowRichExcelModel.setEarnest(NumberUtil.div(projReviewFactoringPrice.getEarnestMoney().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setNominalPrice(BigDecimal.valueOf(0).setScale(2, RoundingMode.UP));
        return cashFlowRichExcelModel;
    }
}
