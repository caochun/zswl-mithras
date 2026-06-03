package cn.zswltech.mithras.service.excel.exporter;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.service.excel.model.CashFlowRichExcelModel;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractFactoringPrice;
import cn.zswltech.mithras.contract.application.dto.ContractEstimateCashFlowExporterBO;
import cn.zswltech.mithras.service.service.contract.ContractFactoringPriceService;
import cn.zswltech.mithras.contract.versioning.application.ContractFactoringPriceLibService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2022/10/31
 * @description
 */
@Component
public class ContractEstimateCashFlowBLExcelExporter extends AbstractContractEstimateCashFlowExcelExporter {
    @Resource
    private ContractFactoringPriceService contractFactoringPriceService;
    @Resource
    private ContractFactoringPriceLibService contractFactoringPriceLibService;

    @Override
    protected CashFlowRichExcelModel prepare(ContractEstimateCashFlowExporterBO contractEstimateCashFlowExporterBO) {
        CashFlowRichExcelModel cashFlowRichExcelModel = super.prepare(contractEstimateCashFlowExporterBO);
        this.decorate(contractEstimateCashFlowExporterBO, cashFlowRichExcelModel);
        cashFlowRichExcelModel.setMoneyCellName("应收保理款（元）");
        return cashFlowRichExcelModel;
    }

    private void decorate(ContractEstimateCashFlowExporterBO contractEstimateCashFlowExporterBO, CashFlowRichExcelModel cashFlowRichExcelModel) {
        ContractBaseInfo contractBaseInfo = contractEstimateCashFlowExporterBO.getContractBaseInfo();
        ContractFactoringPrice contractFactoringPrice;
        if (StrUtil.isBlank(contractEstimateCashFlowExporterBO.getVersion())) {
            contractFactoringPrice = contractFactoringPriceService.getByContractId(contractBaseInfo.getId());
        } else {
            contractFactoringPrice = contractFactoringPriceLibService.getByVersion(contractBaseInfo.getId(), contractEstimateCashFlowExporterBO.getVersion());
        }
        cashFlowRichExcelModel.setMonthCount(contractFactoringPrice.getFactoringCreditTerm());
        cashFlowRichExcelModel.setRentCalculateWay(Optional.ofNullable(RepayCalcType.find(contractFactoringPrice.getRepayCalcType())).map(RepayCalcType::display).orElse(""));
        if (Objects.nonNull(contractFactoringPrice.getLprPercent()) && Objects.nonNull(contractFactoringPrice.getLprAddPercent())) {
            int interestRate = contractFactoringPrice.getLprPercent() + contractFactoringPrice.getLprAddPercent();
            BigDecimal temp = NumberUtil.div(Integer.toString(interestRate), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setInterestRate(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        if (Objects.nonNull(contractFactoringPrice.getIrrPercent())) {
            BigDecimal temp = NumberUtil.div(contractFactoringPrice.getIrrPercent().toString(), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setIrr(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        cashFlowRichExcelModel.setProjectAmount(NumberUtil.div(contractFactoringPrice.getContractAmount().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setConsultingFee(NumberUtil.div(contractFactoringPrice.getConsultingFee().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setEarnest(NumberUtil.div(contractFactoringPrice.getEarnestMoney().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        // 补全第0期
        cashFlowRichExcelModel.getCashFlowList().addFirst(this.getZeroPhaseCashFlow(cashFlowRichExcelModel, null));
        // 修正最后一期现金流 减去保证金
        this.fixLastCashFlow(cashFlowRichExcelModel);
    }
}
