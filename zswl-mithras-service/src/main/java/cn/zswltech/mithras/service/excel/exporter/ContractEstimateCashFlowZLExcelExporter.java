package cn.zswltech.mithras.service.excel.exporter;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.projestablish.PayType;
import cn.zswltech.mithras.service.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.service.excel.model.CashFlowRichExcelModel;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.application.dto.ContractEstimateCashFlowExporterBO;
import cn.zswltech.mithras.service.service.contract.ContractLeasePriceService;
import cn.zswltech.mithras.contract.archive.application.ContractLeasePriceLibService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2022/9/28
 * @description 导出合同概算现金流表（租赁）
 */
@Component
public class ContractEstimateCashFlowZLExcelExporter extends AbstractContractEstimateCashFlowExcelExporter {
    @Resource
    private ContractLeasePriceService contractLeasePriceService;
    @Resource
    private ContractLeasePriceLibService contractLeasePriceLibService;

    @Override
    protected CashFlowRichExcelModel prepare(ContractEstimateCashFlowExporterBO contractEstimateCashFlowExporterBO) {
        CashFlowRichExcelModel cashFlowRichExcelModel = super.prepare(contractEstimateCashFlowExporterBO);
        this.decorate(contractEstimateCashFlowExporterBO, cashFlowRichExcelModel);
        cashFlowRichExcelModel.setMoneyCellName("租金（元）");
        return cashFlowRichExcelModel;
    }

    private void decorate(ContractEstimateCashFlowExporterBO contractEstimateCashFlowExporterBO, CashFlowRichExcelModel cashFlowRichExcelModel) {
        ContractBaseInfo contractBaseInfo = contractEstimateCashFlowExporterBO.getContractBaseInfo();
        ContractLeasePrice contractLeasePrice;
        if (StrUtil.isBlank(contractEstimateCashFlowExporterBO.getVersion())) {
            contractLeasePrice = contractLeasePriceService.getByContractId(contractBaseInfo.getId());
        } else {
            contractLeasePrice = contractLeasePriceLibService.getByVersion(contractBaseInfo.getId(), contractEstimateCashFlowExporterBO.getVersion());
        }
        cashFlowRichExcelModel.setMonthCount(contractLeasePrice.getLeaseMonthCount());
        cashFlowRichExcelModel.setRepayTimesTotal(contractLeasePrice.getRepayTimesTotal());
        cashFlowRichExcelModel.setRentCalculateWay(Optional.ofNullable(RepayCalcType.find(contractLeasePrice.getRentalCalcType())).map(RepayCalcType::display).orElse(""));
        cashFlowRichExcelModel.setPayWay(Optional.ofNullable(PayType.of(contractLeasePrice.getPayType())).map(PayType::display).orElse(""));
        if (Objects.nonNull(contractLeasePrice.getRepayTimesTotal())) {
            // 年还款次数 = (还款期数 * 12) / 租赁期限（月） 除不尽的话向上取整
            int i = contractLeasePrice.getRepayTimesTotal() * 12;
            BigDecimal repayTimesInYear = NumberUtil.div(Integer.valueOf(i), contractLeasePrice.getLeaseMonthCount()).setScale(0, RoundingMode.UP);
            cashFlowRichExcelModel.setRepayTimesInYear(repayTimesInYear.intValue());
        }
        if (Objects.nonNull(contractLeasePrice.getLprPercent()) && Objects.nonNull(contractLeasePrice.getLprAddPercent())) {
            int interestRate = contractLeasePrice.getLprPercent() + contractLeasePrice.getLprAddPercent();
            BigDecimal temp = NumberUtil.div(Integer.toString(interestRate), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setInterestRate(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        if (Objects.nonNull(contractLeasePrice.getIrrPercent())) {
            BigDecimal temp = NumberUtil.div(contractLeasePrice.getIrrPercent().toString(), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setIrr(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        cashFlowRichExcelModel.setProjectAmount(NumberUtil.div(contractLeasePrice.getApplyCreditAmount().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setConsultingFee(NumberUtil.div(contractLeasePrice.getConsultingFee().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setDownPayment(NumberUtil.div(contractLeasePrice.getDownPayment().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setEarnest(NumberUtil.div(contractLeasePrice.getEarnestMoney().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setNominalPrice(NumberUtil.div(contractLeasePrice.getNominalPrice().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setCommission(NumberUtil.div(contractLeasePrice.getCommission().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        // 补全第0期
        cashFlowRichExcelModel.getCashFlowList().addFirst(this.getZeroPhaseCashFlow(cashFlowRichExcelModel, null));
        // 修正最后一期现金流 减去保证金
        this.fixLastCashFlow(cashFlowRichExcelModel);
    }
}
