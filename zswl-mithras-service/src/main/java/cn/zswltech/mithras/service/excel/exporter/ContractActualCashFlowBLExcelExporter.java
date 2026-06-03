package cn.zswltech.mithras.service.excel.exporter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.service.excel.model.CashFlowRichExcelModel;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractFactoringPrice;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.contract.application.dto.ContractActualCashFlowExporterBO;
import cn.zswltech.mithras.service.service.contract.ContractFactoringPriceService;
import cn.zswltech.mithras.contract.versioning.application.ContractFactoringPriceLibService;
import cn.zswltech.mithras.service.util.FinancialUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2022/10/31
 * @description
 */
@Component
public class ContractActualCashFlowBLExcelExporter extends AbstractContractActualCashFlowExcelExporter {
    @Resource
    private ContractFactoringPriceLibService contractFactoringPriceLibService;
    @Resource
    private ContractFactoringPriceService contractFactoringPriceService;

    @Override
    protected CashFlowRichExcelModel prepare(ContractActualCashFlowExporterBO contractActualCashFlowExporterBO) {
        CashFlowRichExcelModel cashFlowRichExcelModel = super.prepare(contractActualCashFlowExporterBO);
        this.decorate(contractActualCashFlowExporterBO, cashFlowRichExcelModel);
        cashFlowRichExcelModel.setMoneyCellName("应收保理款（元）");
        return cashFlowRichExcelModel;
    }

    private void decorate(ContractActualCashFlowExporterBO contractActualCashFlowExporterBO, CashFlowRichExcelModel cashFlowRichExcelModel) {
        Long receiptId;
        ContractFactoringPrice contractFactoringPrice;
        if (contractActualCashFlowExporterBO.isHistory()) {
            ContractBaseInfoLib contractBaseInfoLib = contractActualCashFlowExporterBO.getContractBaseInfoLib();
            receiptId = contractActualCashFlowExporterBO.getContractReceiptLib().getOriginId();
            contractFactoringPrice = contractFactoringPriceLibService.getByVersion(contractBaseInfoLib.getOriginId(), contractActualCashFlowExporterBO.getVersion());
        } else {
            ContractBaseInfo contractBaseInfo = contractActualCashFlowExporterBO.getContractBaseInfo();
            receiptId = contractActualCashFlowExporterBO.getContractReceipt().getId();
            contractFactoringPrice = contractFactoringPriceService.getByContractId(contractBaseInfo.getId());
        }
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.listByReceiptId(receiptId);
        if (CollectionUtil.isNotEmpty(paymentBaseInfoList)) {
            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoList.get(0);
            cashFlowRichExcelModel.setProjectAmount(NumberUtil.div(paymentBaseInfo.getApplyPaymentAmount().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
            cashFlowRichExcelModel.setConsultingFee(NumberUtil.div(paymentBaseInfo.getConsultingFee().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
            cashFlowRichExcelModel.setEarnest(NumberUtil.div(paymentBaseInfo.getEarnestMoney().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        } else {
            cashFlowRichExcelModel.setProjectAmount(NumberUtil.div(contractFactoringPrice.getContractAmount().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
            cashFlowRichExcelModel.setConsultingFee(NumberUtil.div(contractFactoringPrice.getConsultingFee().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
            cashFlowRichExcelModel.setEarnest(NumberUtil.div(contractFactoringPrice.getEarnestMoney().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        }
        cashFlowRichExcelModel.setMonthCount(contractFactoringPrice.getFactoringCreditTerm());
        cashFlowRichExcelModel.setRentCalculateWay(Optional.ofNullable(RepayCalcType.find(contractFactoringPrice.getRepayCalcType())).map(RepayCalcType::display).orElse(""));
        if (StrUtil.isNotBlank(contractFactoringPrice.getRepayRate())) {
            Integer repayTimes = FinancialUtil.getRepayTimesInYear(contractFactoringPrice.getRepayRate());
            if (Objects.nonNull(repayTimes)) {
                cashFlowRichExcelModel.setRepayTimesInYear(repayTimes);
            }
        }
        if (Objects.nonNull(contractFactoringPrice.getLprPercent()) && Objects.nonNull(contractFactoringPrice.getLprAddPercent())) {
            int interestRate = contractFactoringPrice.getLprPercent() + contractFactoringPrice.getLprAddPercent();
            BigDecimal temp = NumberUtil.div(Integer.toString(interestRate), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setInterestRate(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        if (Objects.nonNull(contractFactoringPrice.getIrrPercent())) {
            BigDecimal temp = NumberUtil.div(contractFactoringPrice.getIrrPercent().toString(), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setIrr(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        // 补全现金流
        cashFlowRichExcelModel.getCashFlowList().addFirst(this.getZeroPhaseCashFlow(cashFlowRichExcelModel, receiptId));
        // 修正最后一期现金流 减去保证金
        this.fixLastCashFlow(cashFlowRichExcelModel);
    }
}
