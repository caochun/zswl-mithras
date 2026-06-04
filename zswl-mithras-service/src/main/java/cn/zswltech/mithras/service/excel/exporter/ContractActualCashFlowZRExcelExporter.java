package cn.zswltech.mithras.service.excel.exporter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.service.excel.model.CashFlowRichExcelModel;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAocPrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.contract.application.dto.ContractActualCashFlowExporterBO;
import cn.zswltech.mithras.contract.core.application.ContractAocPriceService;
import cn.zswltech.mithras.contract.versioning.application.ContractAocPriceLibService;
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
public class ContractActualCashFlowZRExcelExporter extends AbstractContractActualCashFlowExcelExporter {
    @Resource
    private ContractAocPriceService contractAocPriceService;
    @Resource
    private ContractAocPriceLibService contractAocPriceLibService;

    @Override
    protected CashFlowRichExcelModel prepare(ContractActualCashFlowExporterBO contractActualCashFlowExporterBO) {
        CashFlowRichExcelModel cashFlowRichExcelModel = super.prepare(contractActualCashFlowExporterBO);
        this.decorate(contractActualCashFlowExporterBO, cashFlowRichExcelModel);
        cashFlowRichExcelModel.setMoneyCellName("回收款（元）");
        return cashFlowRichExcelModel;
    }

    private void decorate(ContractActualCashFlowExporterBO contractActualCashFlowExporterBO, CashFlowRichExcelModel cashFlowRichExcelModel) {
        Long receiptId;
        ContractAocPrice contractAocPrice;
        if (contractActualCashFlowExporterBO.isHistory()) {
            ContractBaseInfoLib contractBaseInfoLib = contractActualCashFlowExporterBO.getContractBaseInfoLib();
            receiptId = contractActualCashFlowExporterBO.getContractReceiptLib().getOriginId();
            contractAocPrice = contractAocPriceLibService.getByVersion(contractBaseInfoLib.getOriginId(), contractActualCashFlowExporterBO.getVersion());
        } else {
            ContractBaseInfo contractBaseInfo = contractActualCashFlowExporterBO.getContractBaseInfo();
            receiptId = contractActualCashFlowExporterBO.getContractReceipt().getId();
            contractAocPrice = contractAocPriceService.getByContractId(contractBaseInfo.getId());
        }
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.listByReceiptId(receiptId);
        if (CollectionUtil.isNotEmpty(paymentBaseInfoList)) {
            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoList.get(0);
            cashFlowRichExcelModel.setProjectAmount(NumberUtil.div(paymentBaseInfo.getApplyPaymentAmount().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
            cashFlowRichExcelModel.setConsultingFee(NumberUtil.div(paymentBaseInfo.getConsultingFee().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
            cashFlowRichExcelModel.setEarnest(NumberUtil.div(paymentBaseInfo.getEarnestMoney().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        } else {
            cashFlowRichExcelModel.setProjectAmount(NumberUtil.div(contractAocPrice.getContractAmount().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
            cashFlowRichExcelModel.setConsultingFee(NumberUtil.div(contractAocPrice.getConsultingFee().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
            cashFlowRichExcelModel.setEarnest(NumberUtil.div(contractAocPrice.getEarnestMoney().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        }
        cashFlowRichExcelModel.setMonthCount(contractAocPrice.getAocCreditTerm());
        cashFlowRichExcelModel.setRentCalculateWay(Optional.ofNullable(RepayCalcType.find(contractAocPrice.getRepayCalcType())).map(RepayCalcType::display).orElse(""));
        if (StrUtil.isNotBlank(contractAocPrice.getRepayRate())) {
            Integer repayTimes = FinancialUtil.getRepayTimesInYear(contractAocPrice.getRepayRate());
            if (Objects.nonNull(repayTimes)) {
                cashFlowRichExcelModel.setRepayTimesInYear(repayTimes);
            }
        }
        if (Objects.nonNull(contractAocPrice.getLprPercent()) && Objects.nonNull(contractAocPrice.getLprAddPercent())) {
            int interestRate = contractAocPrice.getLprPercent() + contractAocPrice.getLprAddPercent();
            BigDecimal temp = NumberUtil.div(Integer.toString(interestRate), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setInterestRate(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        if (Objects.nonNull(contractAocPrice.getIrrPercent())) {
            BigDecimal temp = NumberUtil.div(contractAocPrice.getIrrPercent().toString(), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setIrr(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        // 补全现金流
        cashFlowRichExcelModel.getCashFlowList().addFirst(this.getZeroPhaseCashFlow(cashFlowRichExcelModel, receiptId));
        // 修正最后一期现金流 减去保证金
        this.fixLastCashFlow(cashFlowRichExcelModel);
    }
}
