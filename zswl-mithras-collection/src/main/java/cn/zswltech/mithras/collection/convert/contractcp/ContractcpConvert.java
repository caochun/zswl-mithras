package cn.zswltech.mithras.collection.convert.contractcp;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.dto.contractcp.ContractCollectionPaymentListRSP;
import cn.zswltech.mithras.dto.contractcp.ContractRentActualInfoRSP;
import cn.zswltech.mithras.collection.excel.model.ContractcpCashDetailExcelModel;
import cn.zswltech.mithras.collection.excel.model.ContractcpListExcelModel;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 转换
 *
 * @author wangchuanhao
 * @date 2022/8/22 4:47 PM
 */
public class ContractcpConvert {

    public static ContractcpListExcelModel contractCollectionPaymentListRSP2ExcelModel(ContractCollectionPaymentListRSP rsp) {
        ContractcpListExcelModel contractcpListExcelModel = new ContractcpListExcelModel();
        contractcpListExcelModel.setProjName(rsp.getProjName());
        contractcpListExcelModel.setContractCode(rsp.getContractCode());
        contractcpListExcelModel.setClientName(rsp.getClientName());
        contractcpListExcelModel.setContractStatus(Optional.ofNullable(rsp.getContractStatus()).orElse(""));
        contractcpListExcelModel.setBizType(Optional.ofNullable(rsp.getBizType()).orElse(""));
        contractcpListExcelModel.setProjSponsorUser(rsp.getProjSponsorUser());
        contractcpListExcelModel.setBizDept(rsp.getBizDept());
        contractcpListExcelModel.setReceivedPrincipal(formatAmount(rsp.getReceivedPrincipal()));
        contractcpListExcelModel.setReceivedInterest(formatAmount(rsp.getReceivedInterest()));
        contractcpListExcelModel.setReceivedSum(formatAmount(rsp.getReceivedSum()));
        contractcpListExcelModel.setLastPrincipal(formatAmount(rsp.getLastPrincipal()));
        contractcpListExcelModel.setLastInterest(formatAmount(rsp.getLastInterest()));
        contractcpListExcelModel.setNominalLoanPrice(formatAmount(rsp.getNominalLoanPrice()));
        contractcpListExcelModel.setLastSum(formatAmount(rsp.getLastSum()));
        contractcpListExcelModel.setOverdueAmount(formatAmount(rsp.getOverdueAmount()));
//        contractcpListExcelModel.setOverdueInterest(formatAmount(rsp.getOverdueInterest()));
//        contractcpListExcelModel.setReceivedOverdueInterest(formatAmount(rsp.getReceivedOverdueInterest()));
//        contractcpListExcelModel.setUncollectedOverdueInterest(formatAmount(rsp.getUncollectedOverdueInterest()));
        return contractcpListExcelModel;

    }

    public static ContractcpCashDetailExcelModel contractRentActualInfoRSP2ExcelModel(ContractRentActualInfoRSP rsp) {
        ContractcpCashDetailExcelModel contractcpCashDetailExcelModel = new ContractcpCashDetailExcelModel();
        contractcpCashDetailExcelModel.setRentCode(rsp.getRentCode());
        contractcpCashDetailExcelModel.setWriteOffStatus(rsp.getWriteOffStatus());
        contractcpCashDetailExcelModel.setPhase(rsp.getPhase());
        contractcpCashDetailExcelModel.setCashItem(rsp.getCashItem());
        contractcpCashDetailExcelModel.setRentDate(Optional.ofNullable(rsp.getRentDate()).map(LocalDateTimeUtil::formatNormal).orElse(""));
        contractcpCashDetailExcelModel.setCashFlowAmount(formatAmount(rsp.getCashFlowAmount()));
        contractcpCashDetailExcelModel.setPrincipal(formatAmount(rsp.getPrincipal()));
        contractcpCashDetailExcelModel.setInterest(formatAmount(rsp.getInterest()));
        contractcpCashDetailExcelModel.setPenaltyInterest(formatAmount(rsp.getPenaltyInterest()));
        return contractcpCashDetailExcelModel;
    }

    private static String formatAmount(Long amount) {
        return Optional.ofNullable(amount)
                .map(v -> BigDecimal.valueOf(v).divide(BigDecimal.valueOf(10000L)))
                .map(BigDecimal::toPlainString)
                .orElse("0");
    }
}
