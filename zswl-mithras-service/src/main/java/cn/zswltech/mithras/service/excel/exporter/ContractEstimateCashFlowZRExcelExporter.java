package cn.zswltech.mithras.service.excel.exporter;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.service.excel.model.CashFlowRichExcelModel;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAocPrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.application.dto.ContractEstimateCashFlowExporterBO;
import cn.zswltech.mithras.service.service.contract.ContractAocPriceService;
import cn.zswltech.mithras.contract.versioning.application.ContractAocPriceLibService;
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
public class ContractEstimateCashFlowZRExcelExporter extends AbstractContractEstimateCashFlowExcelExporter {
    @Resource
    private ContractAocPriceService contractAocPriceService;
    @Resource
    private ContractAocPriceLibService contractAocPriceLibService;

    @Override
    protected CashFlowRichExcelModel prepare(ContractEstimateCashFlowExporterBO contractEstimateCashFlowExporterBO) {
        CashFlowRichExcelModel cashFlowRichExcelModel = super.prepare(contractEstimateCashFlowExporterBO);
        this.decorate(contractEstimateCashFlowExporterBO, cashFlowRichExcelModel);
        cashFlowRichExcelModel.setMoneyCellName("回收款（元）");
        return cashFlowRichExcelModel;
    }

    private void decorate(ContractEstimateCashFlowExporterBO contractEstimateCashFlowExporterBO, CashFlowRichExcelModel cashFlowRichExcelModel) {
        ContractBaseInfo contractBaseInfo = contractEstimateCashFlowExporterBO.getContractBaseInfo();
        ContractAocPrice contractAocPrice;
        if (StrUtil.isBlank(contractEstimateCashFlowExporterBO.getVersion())) {
            contractAocPrice = contractAocPriceService.getByContractId(contractBaseInfo.getId());
        } else {
            contractAocPrice = contractAocPriceLibService.getByVersion(contractBaseInfo.getId(), contractEstimateCashFlowExporterBO.getVersion());
        }
        cashFlowRichExcelModel.setMonthCount(contractAocPrice.getAocCreditTerm());
        cashFlowRichExcelModel.setRentCalculateWay(Optional.ofNullable(RepayCalcType.find(contractAocPrice.getRepayCalcType())).map(RepayCalcType::display).orElse(""));
        if (Objects.nonNull(contractAocPrice.getLprPercent()) && Objects.nonNull(contractAocPrice.getLprAddPercent())) {
            int interestRate = contractAocPrice.getLprPercent() + contractAocPrice.getLprAddPercent();
            BigDecimal temp = NumberUtil.div(Integer.toString(interestRate), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setInterestRate(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        if (Objects.nonNull(contractAocPrice.getIrrPercent())) {
            BigDecimal temp = NumberUtil.div(contractAocPrice.getIrrPercent().toString(), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setIrr(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        cashFlowRichExcelModel.setProjectAmount(NumberUtil.div(contractAocPrice.getContractAmount().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setConsultingFee(NumberUtil.div(contractAocPrice.getConsultingFee().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        cashFlowRichExcelModel.setEarnest(NumberUtil.div(contractAocPrice.getEarnestMoney().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        // 补全第0期
        cashFlowRichExcelModel.getCashFlowList().addFirst(this.getZeroPhaseCashFlow(cashFlowRichExcelModel, null));
        // 修正最后一期现金流 减去保证金
        this.fixLastCashFlow(cashFlowRichExcelModel);
    }
}
