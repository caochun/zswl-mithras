package cn.zswltech.mithras.contract.convert.contract;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.zswltech.mithras.dto.contract.rent.ContractRentActualListRSP;
import cn.zswltech.mithras.dto.contract.rent.ContractRentEstimateListRSP;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.service.excel.model.ContractRentActualExcelModel;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentEstimate;
import cn.zswltech.mithras.projectprocess.service.bo.CashFlowBO;
import cn.zswltech.mithras.projectprocess.service.bo.CashFlowCalculateBO;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/8/16
 * @description
 */
@Slf4j
public class ContractRentConvert {
    public static CashFlowBO toCashFlowBO(ContractRentEstimate contractRentEstimate) {
        CashFlowBO cashFlowBO = new CashFlowBO();
        cashFlowBO.setCashFlowDate(contractRentEstimate.getCashFlowDate());
        cashFlowBO.setCashFlowPhase(contractRentEstimate.getCashFlowPhase());
        cashFlowBO.setRent(contractRentEstimate.getRent());
        cashFlowBO.setCashFlowAmount(contractRentEstimate.getRent());
        cashFlowBO.setInterest(contractRentEstimate.getInterest());
        cashFlowBO.setPrincipal(contractRentEstimate.getPrincipal());
        cashFlowBO.setRemainingPrincipal(contractRentEstimate.getRemainingPrincipal());
        return cashFlowBO;
    }

    public static CashFlowBO toCashFlowBO(ContractRentActual contractRentActual) {
        CashFlowBO cashFlowBO = new CashFlowBO();
        cashFlowBO.setCashFlowDate(contractRentActual.getCashFlowDate());
        cashFlowBO.setCashFlowPhase(contractRentActual.getCashFlowPhase());
        cashFlowBO.setRent(contractRentActual.getRent());
        cashFlowBO.setCashFlowAmount(contractRentActual.getRent());
        cashFlowBO.setInterest(contractRentActual.getInterest());
        cashFlowBO.setPrincipal(contractRentActual.getPrincipal());
        cashFlowBO.setRemainingPrincipal(contractRentActual.getRemainingPrincipal());
        return cashFlowBO;
    }

    public static ContractRentEstimate toContractRentEstimate(Long contractId, CashFlowBO cashFlowBO) {
        ContractRentEstimate contractRentEstimate = new ContractRentEstimate();
        contractRentEstimate.setContractId(contractId);
        contractRentEstimate.setCashFlowDate(cashFlowBO.getCashFlowDate());
        contractRentEstimate.setCashFlowPhase(cashFlowBO.getCashFlowPhase());
        contractRentEstimate.setRent(cashFlowBO.getRent());
        contractRentEstimate.setInterest(cashFlowBO.getInterest());
        contractRentEstimate.setPrincipal(cashFlowBO.getPrincipal());
        contractRentEstimate.setRemainingPrincipal(cashFlowBO.getRemainingPrincipal());
        return contractRentEstimate;
    }

    public static CashFlowCalculateBO toCashFlowCalculateBO(ContractLeasePrice leasePrice) {
        CashFlowCalculateBO cashFlowCalculateBO = new CashFlowCalculateBO();
        cashFlowCalculateBO.setCreditAmount(leasePrice.getApplyCreditAmount() == null ? 0L : leasePrice.getApplyCreditAmount());
        cashFlowCalculateBO.setConsultingFee(leasePrice.getConsultingFee() == null ? 0L : leasePrice.getConsultingFee());
        cashFlowCalculateBO.setEarnestMoney(leasePrice.getEarnestMoney() == null ? 0L : leasePrice.getEarnestMoney());
        cashFlowCalculateBO.setDownPayment(leasePrice.getDownPayment() == null ? 0L : leasePrice.getDownPayment());
        cashFlowCalculateBO.setNominalPrice(leasePrice.getNominalPrice() == null ? 0L : leasePrice.getNominalPrice());
        cashFlowCalculateBO.setRepayTimes(leasePrice.getRepayTimesTotal());
        cashFlowCalculateBO.setRepayRate(leasePrice.getRepayRate());
        cashFlowCalculateBO.setInterestRate(leasePrice.getLprPercent() + leasePrice.getLprAddPercent());
        cashFlowCalculateBO.setRentalCalcType(leasePrice.getRentalCalcType());
        cashFlowCalculateBO.setInterestWay(leasePrice.getInterestWay());
        cashFlowCalculateBO.setPayType(leasePrice.getPayType());
        cashFlowCalculateBO.setTotalMonth(leasePrice.getLeaseMonthCount());
        return cashFlowCalculateBO;
    }

    public static ContractRentActualExcelModel toContractRentActualExcelModel(ContractRentActual contractRentActual) {
        ContractRentActualExcelModel contractRentActualExcelModel = new ContractRentActualExcelModel();

        contractRentActualExcelModel.setId(contractRentActual.getId());
        contractRentActualExcelModel.setCashFlowCode(contractRentActual.getCashFlowCode());
        contractRentActualExcelModel.setCashFlowDate(contractRentActual.getCashFlowDate());
        contractRentActualExcelModel.setCashFlowPhase(contractRentActual.getCashFlowPhase());
        if (Objects.isNull(contractRentActual.getRent())) {
            contractRentActual.setRent(0L);
        }
        contractRentActualExcelModel.setRent(NumberUtil.div(contractRentActual.getRent().toString(), GlobalConstants.MONEY_MULTIPLE));
        contractRentActualExcelModel.setCashFlowAmount(contractRentActualExcelModel.getRent());
        if (Objects.isNull(contractRentActual.getPrincipal())) {
            contractRentActual.setPrincipal(0L);
        }
        contractRentActualExcelModel.setPrincipal(NumberUtil.div(contractRentActual.getPrincipal().toString(), GlobalConstants.MONEY_MULTIPLE));
        if (Objects.isNull(contractRentActual.getInterest())) {
            contractRentActual.setInterest(0L);
        }
        contractRentActualExcelModel.setInterest(NumberUtil.div(contractRentActual.getInterest().toString(), GlobalConstants.MONEY_MULTIPLE));
        if (Objects.isNull(contractRentActual.getRemainingPrincipal())) {
            contractRentActual.setRemainingPrincipal(0L);
        }
        contractRentActualExcelModel.setRemainingPrincipal(NumberUtil.div(contractRentActual.getRemainingPrincipal().toString(), GlobalConstants.MONEY_MULTIPLE));
        return contractRentActualExcelModel;
    }

    public static CashFlowExcelModel toCashFlowExcelModel(ContractRentEstimate contractRentEstimate) {
        CashFlowExcelModel cashFlowExcelModel = new CashFlowExcelModel();
        cashFlowExcelModel.setCashFlowDate(contractRentEstimate.getCashFlowDate());
        cashFlowExcelModel.setCashFlowPhase(contractRentEstimate.getCashFlowPhase());
        if (Objects.isNull(contractRentEstimate.getRent())) {
            contractRentEstimate.setRent(0L);
        }
        cashFlowExcelModel.setRent(NumberUtil.div(contractRentEstimate.getRent().toString(), GlobalConstants.MONEY_MULTIPLE));
        cashFlowExcelModel.setCashFlowAmount(cashFlowExcelModel.getRent());
        if (Objects.isNull(contractRentEstimate.getPrincipal())) {
            contractRentEstimate.setPrincipal(0L);
        }
        cashFlowExcelModel.setPrincipal(NumberUtil.div(contractRentEstimate.getPrincipal().toString(), GlobalConstants.MONEY_MULTIPLE));
        if (Objects.isNull(contractRentEstimate.getInterest())) {
            contractRentEstimate.setInterest(0L);
        }
        cashFlowExcelModel.setInterest(NumberUtil.div(contractRentEstimate.getInterest().toString(), GlobalConstants.MONEY_MULTIPLE));
        if (Objects.isNull(contractRentEstimate.getRemainingPrincipal())) {
            contractRentEstimate.setRemainingPrincipal(0L);
        }
        cashFlowExcelModel.setRemainingPrincipal(NumberUtil.div(contractRentEstimate.getRemainingPrincipal().toString(), GlobalConstants.MONEY_MULTIPLE));
        return cashFlowExcelModel;
    }

    public static ContractRentActual toContractRentActual(CashFlowExcelModel cashFlowExcelModel) {
        ContractRentActual contractRentActual = new ContractRentActual();
        contractRentActual.setCashFlowDate(cashFlowExcelModel.getCashFlowDate());
        contractRentActual.setCashFlowPhase(cashFlowExcelModel.getCashFlowPhase());
        if (Objects.nonNull(cashFlowExcelModel.getRent())) {
            contractRentActual.setRent(cashFlowExcelModel.getRent().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
        }
        if (Objects.nonNull(cashFlowExcelModel.getPrincipal())) {
            contractRentActual.setPrincipal(cashFlowExcelModel.getPrincipal().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
        }
        if (Objects.nonNull(cashFlowExcelModel.getInterest())) {
            contractRentActual.setInterest(cashFlowExcelModel.getInterest().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
        }
        if (Objects.nonNull(cashFlowExcelModel.getRemainingPrincipal())) {
            contractRentActual.setRemainingPrincipal(cashFlowExcelModel.getRemainingPrincipal().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
        }
        return contractRentActual;
    }

    public static ContractRentEstimate toContractRentEstimate(CashFlowExcelModel cashFlowExcelModel) {
        ContractRentEstimate contractRentEstimate = new ContractRentEstimate();
        contractRentEstimate.setCashFlowDate(cashFlowExcelModel.getCashFlowDate());
        contractRentEstimate.setCashFlowPhase(cashFlowExcelModel.getCashFlowPhase());
        if (Objects.nonNull(cashFlowExcelModel.getRent())) {
            contractRentEstimate.setRent(cashFlowExcelModel.getRent().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());

        }
        if (Objects.nonNull(cashFlowExcelModel.getPrincipal())) {
            contractRentEstimate.setPrincipal(cashFlowExcelModel.getPrincipal().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
        }
        if (Objects.nonNull(cashFlowExcelModel.getInterest())) {
            contractRentEstimate.setInterest(cashFlowExcelModel.getInterest().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
        }
        if (Objects.nonNull(cashFlowExcelModel.getRemainingPrincipal())) {
            contractRentEstimate.setRemainingPrincipal(cashFlowExcelModel.getRemainingPrincipal().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
        }
        return contractRentEstimate;
    }

    public static ContractRentEstimateListRSP.TableData toContractRentEstimateListRSP(ContractRentEstimate contractRentEstimate) {
        ContractRentEstimateListRSP.TableData tableData = new ContractRentEstimateListRSP.TableData();
        tableData.setId(contractRentEstimate.getId());
        if (Objects.nonNull(contractRentEstimate.getCashFlowDate())) {
            tableData.setDate(LocalDateTimeUtil.format(contractRentEstimate.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN));
        }
        tableData.setPhase(contractRentEstimate.getCashFlowPhase());
        tableData.setRent(contractRentEstimate.getRent());
        tableData.setPrincipal(contractRentEstimate.getPrincipal());
        tableData.setInterest(contractRentEstimate.getInterest());
        tableData.setRemainingPrincipal(contractRentEstimate.getRemainingPrincipal());
        return tableData;
    }

    public static ContractRentActualListRSP.TableData toContractRentActualListTableData(ContractRentActual contractRentActual) {
        ContractRentActualListRSP.TableData tableData = new ContractRentActualListRSP.TableData();
        tableData.setId(contractRentActual.getId());
        tableData.setCashFlowCode(contractRentActual.getCashFlowCode());
        if (Objects.nonNull(contractRentActual.getCashFlowDate())) {
            tableData.setDate(LocalDateTimeUtil.format(contractRentActual.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN));
        }
        tableData.setPhase(contractRentActual.getCashFlowPhase());
        tableData.setRent(contractRentActual.getRent());
        tableData.setPrincipal(contractRentActual.getPrincipal());
        tableData.setInterest(contractRentActual.getInterest());
        tableData.setRemainingPrincipal(contractRentActual.getRemainingPrincipal());
        tableData.setReceivedDate(contractRentActual.getCollectionDate());
        tableData.setReceivedAmount(contractRentActual.getCollectionAmount());
        return tableData;
    }
}
