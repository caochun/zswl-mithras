package cn.zswltech.mithras.contract.excel.exporter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.projectprocess.enums.projestablish.PayType;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.projectprocess.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.projectprocess.excel.model.CashFlowRichExcelModel;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.core.dto.ContractActualCashFlowExporterBO;
import cn.zswltech.mithras.contract.core.ContractLeasePriceService;
import cn.zswltech.mithras.contract.versioning.application.ContractLeasePriceLibService;
import cn.zswltech.mithras.projectprocess.excel.payment.PaymentActualDetailData;
import cn.zswltech.mithras.projectprocess.excel.payment.PaymentBaseInfoData;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/10/31
 * @description
 */
@Component
public class ContractActualCashFlowZLExcelExporter extends AbstractContractActualCashFlowExcelExporter {
    @Resource
    private ContractLeasePriceService contractLeasePriceService;
    @Resource
    private ContractLeasePriceLibService contractLeasePriceLibService;

    @Override
    protected CashFlowRichExcelModel prepare(ContractActualCashFlowExporterBO contractActualCashFlowExporterBO) {
        CashFlowRichExcelModel cashFlowRichExcelModel = super.prepare(contractActualCashFlowExporterBO);
        this.decorate(contractActualCashFlowExporterBO, cashFlowRichExcelModel);
        cashFlowRichExcelModel.setMoneyCellName("租金（元）");
        return cashFlowRichExcelModel;
    }

    private void decorate(ContractActualCashFlowExporterBO contractActualCashFlowExporterBO, CashFlowRichExcelModel cashFlowRichExcelModel) {
        Long receiptId;
        ContractLeasePrice contractLeasePrice;
        boolean isZhizu;
        if (contractActualCashFlowExporterBO.isHistory()) {
            ContractBaseInfoLib contractBaseInfoLib = contractActualCashFlowExporterBO.getContractBaseInfoLib();
            isZhizu = Objects.equals(contractBaseInfoLib.getLeaseType(), LeaseType.zhi_zu.name());
            receiptId = contractActualCashFlowExporterBO.getContractReceiptLib().getOriginId();
            contractLeasePrice = contractLeasePriceLibService.getByVersion(contractBaseInfoLib.getOriginId(), contractActualCashFlowExporterBO.getVersion());
        } else {
            ContractBaseInfo contractBaseInfo = contractActualCashFlowExporterBO.getContractBaseInfo();
            isZhizu = Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name());
            receiptId = contractActualCashFlowExporterBO.getContractReceipt().getId();
            contractLeasePrice = contractLeasePriceService.getByContractId(contractBaseInfo.getId());
        }
        List<PaymentBaseInfoData> paymentBaseInfoList = paymentCashFlowQueryPort.listPaymentBaseInfoByReceiptId(receiptId);
        // 费率明细
        if (!isZhizu && CollectionUtil.isNotEmpty(paymentBaseInfoList)) {
            PaymentBaseInfoData paymentBaseInfo = paymentBaseInfoList.get(0);
            cashFlowRichExcelModel.setProjectAmount(NumberUtil.div(paymentBaseInfo.getApplyPaymentAmount().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
            cashFlowRichExcelModel.setConsultingFee(NumberUtil.div(paymentBaseInfo.getConsultingFee().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
            cashFlowRichExcelModel.setDownPayment(NumberUtil.div(paymentBaseInfo.getDownPayment().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
            cashFlowRichExcelModel.setEarnest(NumberUtil.div(paymentBaseInfo.getEarnestMoney().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
            cashFlowRichExcelModel.setNominalPrice(NumberUtil.div(paymentBaseInfo.getNominalPrice().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
        } else {
            cashFlowRichExcelModel.setProjectAmount(NumberUtil.div(contractLeasePrice.getApplyCreditAmount().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
            cashFlowRichExcelModel.setConsultingFee(NumberUtil.div(contractLeasePrice.getConsultingFee().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
            cashFlowRichExcelModel.setDownPayment(NumberUtil.div(contractLeasePrice.getDownPayment().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
            cashFlowRichExcelModel.setEarnest(NumberUtil.div(contractLeasePrice.getEarnestMoney().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
            cashFlowRichExcelModel.setNominalPrice(NumberUtil.div(contractLeasePrice.getNominalPrice().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
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
        if (!isZhizu && Objects.nonNull(contractLeasePrice.getIrrPercent())) {
            BigDecimal temp = NumberUtil.div(contractLeasePrice.getIrrPercent().toString(), GlobalConstants.MONEY_MULTIPLE);
            cashFlowRichExcelModel.setIrr(NumberUtil.div(temp, 100).setScale(4, RoundingMode.HALF_UP));
        }
        // 补全现金流
        if (isZhizu) {
            // 在实际租金表中插入投放的现金流
            // 1. 从【付款核销】模块，取“付款记录明细”-“实付日期”，只取第一笔（日期最早）的日期
            // 2. 按【付款申请】的条数，一条付款申请对应一条现金流流水
            // 3. 根据日期从小到大的顺序，将该笔付款流水自动插入到合适的row
            // 现金流金额 = 已核销的实付金额加总
            if (CollectionUtil.isNotEmpty(paymentBaseInfoList)) {
                // 查询付款核销明细
                Map<Long, PaymentBaseInfoData> paymentBaseInfoMap = paymentBaseInfoList.stream().collect(Collectors.toMap(PaymentBaseInfoData::getId, e -> e));
                Map<Long, List<PaymentActualDetailData>> paymentActualDetailMap = paymentCashFlowQueryPort.mapActualDetailByPaymentIds(paymentBaseInfoMap.keySet());
                // 插入投放现金流
                for (Map.Entry<Long, PaymentBaseInfoData> entry : paymentBaseInfoMap.entrySet()) {
                    List<PaymentActualDetailData> paymentActualDetailList = paymentActualDetailMap.get(entry.getKey());
                    if (CollectionUtil.isEmpty(paymentActualDetailList)) {
                        continue;
                    }
                    paymentActualDetailList.sort(Comparator.comparing(PaymentActualDetailData::getPaidInDate));
                    CashFlowExcelModel cashFlowExcelModel = new CashFlowExcelModel();
                    cashFlowExcelModel.setCashFlowDate(paymentActualDetailList.get(0).getPaidInDate());
                    // 计算合计金额
                    long amount = paymentActualDetailList.stream().mapToLong(PaymentActualDetailData::getPaidInAmount).sum();
                    cashFlowExcelModel.setCashFlowAmount(NumberUtil.div(String.valueOf(amount * -1), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
                    cashFlowRichExcelModel.getCashFlowList().add(cashFlowExcelModel);
                }
                // 重新排序
                cashFlowRichExcelModel.getCashFlowList().sort(Comparator.comparing(CashFlowExcelModel::getCashFlowDate));
            }
        } else {
            cashFlowRichExcelModel.setCommission(NumberUtil.div(contractLeasePrice.getCommission().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
            cashFlowRichExcelModel.getCashFlowList().addFirst(this.getZeroPhaseCashFlow(cashFlowRichExcelModel, receiptId));
        }
        // 修正最后一期现金流 减去保证金
        this.fixLastCashFlow(cashFlowRichExcelModel);
    }
}
