package cn.zswltech.mithras.fund.application.convert.receiptrepay;

import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.fund.financing.baseinfo.FundFinancingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.fund.financing.plan.FundFinancingPlanDetailRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBaseInfoListREQ;
import cn.zswltech.mithras.fund.application.convert.FundTypeConversionWorker;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingPayAccount;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.fund.model.financing.FundFinancingCollectAccountLib;
import cn.zswltech.mithras.fund.model.financing.FundFinancingPayAccountLib;
import cn.zswltech.mithras.fund.model.financing.FundFinancingRepayActualLib;
import cn.zswltech.mithras.fund.model.receiptrepay.*;
import cn.zswltech.mithras.fund.application.receiptrepay.FundReceiptRepayListQueryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/21 10:23
 */
@Mapper(componentModel = "spring", uses = FundTypeConversionWorker.class)
public interface FundReceiptRepayConverter {
    @Mapping(target = "dateFrom", source = "dateFrom", qualifiedByName = "startOfDay")
    @Mapping(target = "dateTo", source = "dateTo", qualifiedByName = "endOfDay")
    @Mapping(target = "createTimeFrom", source = "createTimeFrom", qualifiedByName = "startOfDay")
    @Mapping(target = "createTimeTo", source = "createTimeTo", qualifiedByName = "endOfDay")
    @Mapping(target = "updateTimeFrom", source = "updateTimeFrom", qualifiedByName = "startOfDay")
    @Mapping(target = "updateTimeTo", source = "updateTimeTo", qualifiedByName = "endOfDay")
    FundReceiptRepayListQueryDto listReq2QueryDto(FundReceiptRepayBaseInfoListREQ req);


    @Mapping(target = "id", source = "receiptRepayBaseInfo.id")
    @Mapping(target = "receiptRepayCode", source = "receiptRepayBaseInfo.receiptRepayCode")
    @Mapping(target = "remark", source = "receiptRepayBaseInfo.remark")

//    @Mapping(target = "financingOrgName", source = "financingBaseInfo.organizationNames")
    @Mapping(target = "totalCreditLimit", source = "financingBaseInfo.totalCreditLimit")
    @Mapping(target = "remainingCreditLimit", source = "financingBaseInfo.remainingCreditLimit")
    @Mapping(target = "fundManager", source = "financingBaseInfo.fundManagerName")
    @Mapping(target = "department", source = "financingBaseInfo.deptName")
    @Mapping(target = "departmentLeader", source = "financingBaseInfo.bizHeaderName")
    @Mapping(target = "chargeLeader", source = "financingBaseInfo.leaderName")
    @Mapping(target = "guaranteeDetail", source = "financingBaseInfo.guaranteeInfoList")

    @Mapping(target = "financingAmount", source = "financingPlan.financingAmount")
    FundReceiptRepayBaseInfoDetailRSP joinDetailRsp(FundReceiptRepayBaseInfo receiptRepayBaseInfo, FundFinancingBaseInfoDetailRSP financingBaseInfo, FundFinancingPlanDetailRSP financingPlan);

    @Mapping(target = "id", source = "receiptRepayBaseInfo.id")
    @Mapping(target = "receiptRepayCode", source = "receiptRepayBaseInfo.receiptRepayCode")
    @Mapping(target = "remark", source = "receiptRepayBaseInfo.remark")
    @Mapping(target = "fundManager", source = "financingBaseInfo.fundManagerName")
    @Mapping(target = "department", source = "financingBaseInfo.deptName")
    @Mapping(target = "departmentLeader", source = "financingBaseInfo.bizHeaderName")
    @Mapping(target = "chargeLeader", source = "financingBaseInfo.leaderName")
    @Mapping(target = "financingAmount", source = "receiptRepayBaseInfo.financingAmount")
//    @Mapping(target = "financingOrgName", source = "receiptRepayBaseInfo.financingOrgNames", qualifiedByName = "jsonStringToStringList")
    FundReceiptRepayBaseInfoDetailRSP joinDirectDetailRsp(FundReceiptRepayBaseInfo receiptRepayBaseInfo, FundDirectFinancingBaseInfoDetailRSP financingBaseInfo);

    FundReceiptRepayCashFlow repayActual2CashFlow(FundFinancingRepayActualLib repayActualLib);

    FundReceiptRepayCashFlow repayActual2CashFlow(FundDirectFinancingRepayActual repayActual);

    FundReceiptRepayAccount accountLib2Account(FundFinancingPayAccountLib accountLib);

    FundRepayAccount copyPayAccount(FundFinancingPayAccountLib payAccount);

    FundRepayAccount copyPayAccount(FundDirectFinancingPayAccount payAccount);

    FundReceiptAccount copyCollectAccount(FundFinancingCollectAccountLib collectAccounts);

    @Mapping(target = "id", source = "originId")
    FundReceiptRepayBaseInfo lib2Entity(FundReceiptRepayBaseInfoLib baseInfoLib);
}
