package cn.zswltech.mithras.service.excel.exporter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.projectprocess.convert.projreview.ProjReviewCashFlowPlanConverter;
import cn.zswltech.mithras.projectprocess.enums.projestablish.PayType;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.service.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.service.excel.model.CashFlowRichExcelModel;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewCashFlowPlan;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewLeasePriceLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.projectprocess.service.bo.ProjReviewCashFlowExporterBO;
import cn.zswltech.mithras.projectprocess.service.lib.projreview.ProjReviewLeasePriceLibService;
import cn.zswltech.mithras.projectprocess.service.lib.projreview.handler.impl.ProjReviewLeasePriceLibHandler;
import cn.zswltech.mithras.service.service.projreview.ProjReviewLeasePriceService;
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
public class ProjReviewCashFlowZLExcelExporter extends AbstractCashFlowExcelExporter<ProjReviewCashFlowExporterBO> {
    @Resource
    private ProjReviewLeasePriceService projReviewLeasePriceService;
    @Resource
    private ProjReviewLeasePriceLibService projReviewLeasePriceLibService;
    @Resource
    private ProjReviewLeasePriceLibHandler projReviewLeasePriceLibHandler;

    @Override
    protected CashFlowRichExcelModel prepare(ProjReviewCashFlowExporterBO projReviewCashFlowExporterBO) {
        // 查询报价方案
        CashFlowRichExcelModel cashFlowRichExcelModel = this.assemblyZL(projReviewCashFlowExporterBO);
        // 现金流计划表
        List<ProjReviewCashFlowPlan> projReviewCashFlowPlanList = projReviewCashFlowExporterBO.getProjReviewCashFlowPlanList();
        if (CollectionUtil.isNotEmpty(projReviewCashFlowPlanList)) {
            List<CashFlowExcelModel> cashFlowExcelModelList = projReviewCashFlowPlanList.stream().map(ProjReviewCashFlowPlanConverter::toCashFlowExcelModel).collect(Collectors.toList());
            cashFlowRichExcelModel.setCashFlowList(new LinkedList<>(cashFlowExcelModelList));
            cashFlowRichExcelModel.getCashFlowList().addFirst(this.getZeroPhaseCashFlow(cashFlowRichExcelModel, null));
        }
        // 特殊表头指定
        cashFlowRichExcelModel.setMoneyCellName("租金（元）");
        return cashFlowRichExcelModel;
    }

    private CashFlowRichExcelModel assemblyZL(ProjReviewCashFlowExporterBO projReviewCashFlowExporterBO) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewCashFlowExporterBO.getProjReviewBaseInfo();
        ProjReviewLeasePrice projReviewLeasePrice;
        if (StrUtil.isBlank(projReviewCashFlowExporterBO.getVersion())) {
            projReviewLeasePrice = projReviewLeasePriceService.getByProjectId(projReviewBaseInfo.getId());
            Assert.notNull(projReviewLeasePrice, () -> MithrasException.newException("请先保存报价方案"));
        } else {
            ProjReviewLeasePriceLib projReviewLeasePriceLib = projReviewLeasePriceLibService.getByProjReviewIdAndVersion(projReviewBaseInfo.getId(), projReviewCashFlowExporterBO.getVersion());
            Assert.notNull(projReviewLeasePriceLib, () -> MithrasException.newException("对应版本的报价方案数据不存在"));
            projReviewLeasePrice = projReviewLeasePriceLibHandler.actualLib2Entity(projReviewLeasePriceLib);
        }
        CashFlowRichExcelModel cashFlowRichExcelModel = new CashFlowRichExcelModel();
        cashFlowRichExcelModel.setBizType(projReviewBaseInfo.getBizType());
        cashFlowRichExcelModel.setStartRentDate(projReviewLeasePrice.getPlannedStartingDate());
        cashFlowRichExcelModel.setMonthCount(projReviewLeasePrice.getLeaseMonthCount());
        cashFlowRichExcelModel.setRepayTimesTotal(projReviewLeasePrice.getRepayTimesTotal());
        if (Objects.nonNull(projReviewLeasePrice.getRepayTimesTotal())) {
            // 年还款次数 = (还款期数 * 12) / 租赁期限（月） 除不尽的话向上取整
            int i = projReviewLeasePrice.getRepayTimesTotal() * 12;
            BigDecimal repayTimesInYear = NumberUtil.div(Integer.valueOf(i), projReviewLeasePrice.getLeaseMonthCount()).setScale(0, RoundingMode.UP);
            cashFlowRichExcelModel.setRepayTimesInYear(repayTimesInYear.intValue());
        }
        cashFlowRichExcelModel.setRentCalculateWay(Optional.ofNullable(RepayCalcType.find(projReviewLeasePrice.getRentalCalcType())).map(RepayCalcType::display).orElse(""));
        cashFlowRichExcelModel.setPayWay(Optional.ofNullable(PayType.of(projReviewLeasePrice.getPayType())).map(PayType::display).orElse(""));
        if (Objects.nonNull(projReviewLeasePrice.getLeaseRatePercent())) {
            BigDecimal temp = NumberUtil.div(projReviewLeasePrice.getLeaseRatePercent().toString(), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setInterestRate(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        if (Objects.nonNull(projReviewLeasePrice.getIrrPercent())) {
            BigDecimal temp = NumberUtil.div(projReviewLeasePrice.getIrrPercent().toString(), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setIrr(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        cashFlowRichExcelModel.setProjectAmount(NumberUtil.div(projReviewLeasePrice.getApplyCreditAmount().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setConsultingFee(NumberUtil.div(projReviewLeasePrice.getConsultingFee().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setDownPayment(NumberUtil.div(projReviewLeasePrice.getDownPayment().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setEarnest(NumberUtil.div(projReviewLeasePrice.getEarnestMoney().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setNominalPrice(NumberUtil.div(projReviewLeasePrice.getNominalPrice().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        return cashFlowRichExcelModel;
    }
}
