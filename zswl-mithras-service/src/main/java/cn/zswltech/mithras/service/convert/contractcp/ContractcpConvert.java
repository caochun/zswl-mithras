package cn.zswltech.mithras.service.convert.contractcp;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.dto.contractcp.ContractCollectionPaymentListRSP;
import cn.zswltech.mithras.dto.contractcp.ContractRentActualInfoRSP;
import cn.zswltech.mithras.common.enums.ProjectBizType;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.excel.model.ContractcpCashDetailExcelModel;
import cn.zswltech.mithras.service.excel.model.ContractcpListExcelModel;
import cn.zswltech.mithras.service.others.Util;
import org.springframework.stereotype.Component;

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
        contractcpListExcelModel.setReceivedPrincipal(Optional.ofNullable(rsp.getReceivedPrincipal()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
        contractcpListExcelModel.setReceivedInterest(Optional.ofNullable(rsp.getReceivedInterest()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
        contractcpListExcelModel.setReceivedSum(Optional.ofNullable(rsp.getReceivedSum()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
        contractcpListExcelModel.setLastPrincipal(Optional.ofNullable(rsp.getLastPrincipal()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
        contractcpListExcelModel.setLastInterest(Optional.ofNullable(rsp.getLastInterest()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
        contractcpListExcelModel.setNominalLoanPrice(Optional.ofNullable(rsp.getNominalLoanPrice()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
        contractcpListExcelModel.setLastSum(Optional.ofNullable(rsp.getLastSum()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
        contractcpListExcelModel.setOverdueAmount(Optional.ofNullable(rsp.getOverdueAmount()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
//        contractcpListExcelModel.setOverdueInterest(Optional.ofNullable(rsp.getOverdueInterest()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
//        contractcpListExcelModel.setReceivedOverdueInterest(Optional.ofNullable(rsp.getReceivedOverdueInterest()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
//        contractcpListExcelModel.setUncollectedOverdueInterest(Optional.ofNullable(rsp.getUncollectedOverdueInterest()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
        return contractcpListExcelModel;

    }

    public static ContractcpCashDetailExcelModel contractRentActualInfoRSP2ExcelModel(ContractRentActualInfoRSP rsp) {
        ContractcpCashDetailExcelModel contractcpCashDetailExcelModel = new ContractcpCashDetailExcelModel();
        contractcpCashDetailExcelModel.setRentCode(rsp.getRentCode());
        contractcpCashDetailExcelModel.setWriteOffStatus(rsp.getWriteOffStatus());
        contractcpCashDetailExcelModel.setPhase(rsp.getPhase());
        contractcpCashDetailExcelModel.setCashItem(rsp.getCashItem());
        contractcpCashDetailExcelModel.setRentDate(Optional.ofNullable(rsp.getRentDate()).map(LocalDateTimeUtil::formatNormal).orElse(""));
        contractcpCashDetailExcelModel.setCashFlowAmount(Optional.ofNullable(rsp.getCashFlowAmount()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
        contractcpCashDetailExcelModel.setPrincipal(Optional.ofNullable(rsp.getPrincipal()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
        contractcpCashDetailExcelModel.setInterest(Optional.ofNullable(rsp.getInterest()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
        contractcpCashDetailExcelModel.setPenaltyInterest(Optional.ofNullable(rsp.getPenaltyInterest()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
        return contractcpCashDetailExcelModel;
    }
}
