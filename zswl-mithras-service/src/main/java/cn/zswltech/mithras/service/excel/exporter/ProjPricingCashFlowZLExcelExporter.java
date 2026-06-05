package cn.zswltech.mithras.service.excel.exporter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.projectprocess.convert.projpricing.ProjPricingCashFlowPlanConverter;
import cn.zswltech.mithras.projectprocess.enums.projestablish.PayType;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.service.excel.exporter.AbstractCashFlowExcelExporter;
import cn.zswltech.mithras.projectprocess.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.projectprocess.excel.model.CashFlowRichExcelModel;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingCashFlowPlan;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingLeasePrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingLeasePriceLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.projectprocess.service.bo.ProjPricingCashFlowExporterBO;
import cn.zswltech.mithras.projectprocess.service.lib.projpricing.ProjPricingLeasePriceLibService;
import cn.zswltech.mithras.projectprocess.service.lib.projpricing.handler.impl.ProjPricingLeasePriceLibHandler;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingLeasePriceService;
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
public class ProjPricingCashFlowZLExcelExporter extends AbstractCashFlowExcelExporter<ProjPricingCashFlowExporterBO> {
    @Resource
    private ProjPricingLeasePriceService projPricingLeasePriceService;
    @Resource
    private ProjPricingLeasePriceLibService projPricingLeasePriceLibService;
    @Resource
    private ProjPricingLeasePriceLibHandler projPricingLeasePriceLibHandler;

    @Override
    protected CashFlowRichExcelModel prepare(ProjPricingCashFlowExporterBO projPricingCashFlowExporterBO) {
        // 查询报价方案
        CashFlowRichExcelModel cashFlowRichExcelModel = this.assemblyZL(projPricingCashFlowExporterBO);
        // 现金流计划表
        List<ProjPricingCashFlowPlan> projPricingCashFlowPlanList = projPricingCashFlowExporterBO.getProjPricingCashFlowPlanList();
        if (CollectionUtil.isNotEmpty(projPricingCashFlowPlanList)) {
            List<CashFlowExcelModel> cashFlowExcelModelList = projPricingCashFlowPlanList.stream().map(ProjPricingCashFlowPlanConverter::toCashFlowExcelModel).collect(Collectors.toList());
            cashFlowRichExcelModel.setCashFlowList(new LinkedList<>(cashFlowExcelModelList));
            cashFlowRichExcelModel.getCashFlowList().addFirst(this.getZeroPhaseCashFlow(cashFlowRichExcelModel, null));
        }
        // 特殊表头指定
        cashFlowRichExcelModel.setMoneyCellName("租金（元）");
        return cashFlowRichExcelModel;
    }

    private CashFlowRichExcelModel assemblyZL(ProjPricingCashFlowExporterBO projPricingCashFlowExporterBO) {
        ProjPricingBaseInfo projPricingBaseInfo = projPricingCashFlowExporterBO.getProjPricingBaseInfo();
        ProjPricingLeasePrice projPricingLeasePrice;
        if (StrUtil.isBlank(projPricingCashFlowExporterBO.getVersion())) {
            projPricingLeasePrice = projPricingLeasePriceService.getByProjectId(projPricingBaseInfo.getId());
            Assert.notNull(projPricingLeasePrice, () -> MithrasException.newException("请先保存报价方案"));
        } else {
            ProjPricingLeasePriceLib projPricingLeasePriceLib = projPricingLeasePriceLibService.getByProjPricingIdAndVersion(projPricingBaseInfo.getId(), projPricingCashFlowExporterBO.getVersion());
            Assert.notNull(projPricingLeasePriceLib, () -> MithrasException.newException("对应版本的报价方案数据不存在"));
            projPricingLeasePrice = projPricingLeasePriceLibHandler.actualLib2Entity(projPricingLeasePriceLib);
        }
        CashFlowRichExcelModel cashFlowRichExcelModel = new CashFlowRichExcelModel();
        cashFlowRichExcelModel.setBizType(projPricingBaseInfo.getBizType());
        cashFlowRichExcelModel.setStartRentDate(projPricingLeasePrice.getPlannedStartingDate());
        cashFlowRichExcelModel.setMonthCount(projPricingLeasePrice.getLeaseMonthCount());
        cashFlowRichExcelModel.setRepayTimesTotal(projPricingLeasePrice.getRepayTimesTotal());
        if (Objects.nonNull(projPricingLeasePrice.getRepayTimesTotal())) {
            // 年还款次数 = (还款期数 * 12) / 租赁期限（月） 除不尽的话向上取整
            int i = projPricingLeasePrice.getRepayTimesTotal() * 12;
            BigDecimal repayTimesInYear = NumberUtil.div(Integer.valueOf(i), projPricingLeasePrice.getLeaseMonthCount()).setScale(0, RoundingMode.UP);
            cashFlowRichExcelModel.setRepayTimesInYear(repayTimesInYear.intValue());
        }
        cashFlowRichExcelModel.setRentCalculateWay(Optional.ofNullable(RepayCalcType.find(projPricingLeasePrice.getRentalCalcType())).map(RepayCalcType::display).orElse(""));
        cashFlowRichExcelModel.setPayWay(Optional.ofNullable(PayType.of(projPricingLeasePrice.getPayType())).map(PayType::display).orElse(""));
        if (Objects.nonNull(projPricingLeasePrice.getLeaseRatePercent())) {
            BigDecimal temp = NumberUtil.div(projPricingLeasePrice.getLeaseRatePercent().toString(), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setInterestRate(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        if (Objects.nonNull(projPricingLeasePrice.getIrrPercent())) {
            BigDecimal temp = NumberUtil.div(projPricingLeasePrice.getIrrPercent().toString(), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setIrr(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        cashFlowRichExcelModel.setProjectAmount(NumberUtil.div(projPricingLeasePrice.getApplyCreditAmount().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setConsultingFee(NumberUtil.div(projPricingLeasePrice.getConsultingFee().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setDownPayment(NumberUtil.div(projPricingLeasePrice.getDownPayment().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setEarnest(NumberUtil.div(projPricingLeasePrice.getEarnestMoney().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setNominalPrice(NumberUtil.div(projPricingLeasePrice.getNominalPrice().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        return cashFlowRichExcelModel;
    }
}
