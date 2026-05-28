package cn.zswltech.mithras.service.convert.payment;

import cn.zswltech.mithras.api.payment.dto.*;
import cn.zswltech.mithras.api.payment.writeoff.ActualDetailListRsp;
import cn.zswltech.mithras.api.payment.writeoff.PaymentWriteOffDetailRsp;
import cn.zswltech.mithras.api.payment.writeoff.PaymentWriteOffListReq;
import cn.zswltech.mithras.api.payment.writeoff.PaymentWriteOffListRsp;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoListREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.third.financial.ThirdPaymentDetailREQ;
import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import cn.zswltech.mithras.service.excel.model.PaymentPolicyExcelModel;
import cn.zswltech.mithras.service.mapper.dto.PaymentListDto;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentPolicyInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 16:45
 */
@Mapper(componentModel = "spring", uses = TypeConversionWorker.class)
public interface PaymentConvert {

    PaymentBaseInfo addReqToEntity(PaymentAddReq req);

    @Mapping(target = "applyPaymentDate", source = "applyPaymentDate", qualifiedByName = "startOfDay")
    PaymentBaseInfo modifyReqToEntity(PaymentModifyReq req);

    @Mapping(target = "applyPaymentDateFrom", source = "applyPaymentDateFrom", qualifiedByName = "startOfDay")
    @Mapping(target = "applyPaymentDateTo", source = "applyPaymentDateTo", qualifiedByName = "endOfDay")
    @Mapping(target = "paidInDateFrom", source = "paidInDateFrom", qualifiedByName = "startOfDay")
    @Mapping(target = "paidInDateTo", source = "paidInDateTo", qualifiedByName = "endOfDay")
    PaymentListDto listReqToListDto(PaymentListReq req);

    PaymentListRsp entityToListRsp(PaymentBaseInfo record);

    ContractBaseInfoListREQ paymentListReqToListReq(PaymentContractListReq req);

    @Mapping(target = "planedPaidAmount", source = "applyCreditAmount")
    @Mapping(target = "planedPaidDate", source = "paymentPlanDate")
    @Mapping(target = "contractId", source = "originId")
    PaymentContractListRsp listRspToPaymentListRsp(ContractBaseInfoLib record);

    @Mapping(target = "contractId", source = "contractDetail.id")
    @Mapping(target = "contractCode", source = "contractDetail.contractCode")
    @Mapping(target = "clientId", source = "contractDetail.clientId")
    @Mapping(target = "clientName", source = "contractDetail.clientName")
    @Mapping(target = "projName", source = "contractDetail.projName")
    @Mapping(target = "projCode", source = "contractDetail.projCode")
    @Mapping(target = "leaseType", source = "contractDetail.leaseType")
    @Mapping(target = "projSponsorUserId", source = "contractDetail.projSponsorUserId")
    @Mapping(target = "projSponsorUserName", source = "contractDetail.projSponsorUserName")
    @Mapping(target = "bizDeptId", source = "contractDetail.bizDeptId")
    @Mapping(target = "bizDeptName", source = "contractDetail.bizDeptName")
    @Mapping(target = "planedPaidDate", source = "contractDetail.paymentPlanDate")
    @Mapping(target = "projReviewId", source = "contractDetail.projReviewId")
//    @Mapping(target = "contractEarnestMoney", source = "priceDetail.leasePriceModifyRSP.earnestMoney")
//    @Mapping(target = "contractNominalPrice", source = "priceDetail.leasePriceModifyRSP.nominalPrice")
//    @Mapping(target = "contractConsultingFee", source = "priceDetail.leasePriceModifyRSP.consultingFee")
//    @Mapping(target = "applyCreditAmount", source = "priceDetail.leasePriceModifyRSP.applyCreditAmount")
//    @Mapping(target = "contractDownPayment", source = "priceDetail.leasePriceModifyRSP.downPayment")
//    @Mapping(target = "planedPaidAmount", source = "priceDetail.leasePriceModifyRSP.applyCreditAmount")
    @Mapping(target = "id", source = "payment.id")
    @Mapping(target = "paymentStatus", source = "payment.paymentStatus")
    @Mapping(target = "applyPaymentDate", source = "payment.applyPaymentDate")
    @Mapping(target = "applyPaymentAmount", source = "payment.applyPaymentAmount")
    @Mapping(target = "writeOffStatus", source = "payment.writeOffStatus")
    @Mapping(target = "writeOffUserIds", source = "payment.writeOffUserIds", qualifiedByName = "jsonStringToLongList")
    @Mapping(target = "earnestMoney", source = "payment.earnestMoney")
    @Mapping(target = "nominalPrice", source = "payment.nominalPrice")
    @Mapping(target = "consultingFee", source = "payment.consultingFee")
    @Mapping(target = "commission", source = "payment.commission")
    @Mapping(target = "firstInstallmentInterest", source = "payment.firstInstallmentInterest")
    @Mapping(target = "downPayment", source = "payment.downPayment")
    @Mapping(target = "processStatus", source = "payment.paymentProcessStatus")
    @Mapping(target = "remark", source = "payment.remark")
    @Mapping(target = "downPaymentType", source = "payment.downPaymentType")
    @Mapping(target = "retentionMoney", source = "payment.retentionMoney")
    @Mapping(target = "retentionMoneyType", source = "payment.retentionMoneyType")
    @Mapping(target = "receiptId", source = "payment.receiptId")
    @Mapping(target = "leasedCurrency", source = "payment.leasedCurrency")
    @Mapping(target = "leasedPrice", source = "payment.leasedPrice")
    PaymentDetailRsp joinPaymentDetail(ContractBaseInfoDetailRSP contractDetail, ContractPriceDetailRSP priceDetail, PaymentBaseInfo payment);

    PaymentListReq writeOffListReqToPaymentListReq(PaymentWriteOffListReq req);

    @Mapping(target = "paymentId", source = "id")
    PaymentWriteOffListRsp paymentListRspToWriteOffListRsp(PaymentListRsp paymentListRsp);

    PaymentWriteOffDetailRsp paymentDetailToWriteoffDetail(PaymentDetailRsp detail);

    @Mapping(target = "payableAmount", source = "applyPaymentAmount")
    @Mapping(target = "writeOffUserIds", source = "writeOffUserIds", qualifiedByName = "jsonStringToLongList")
    @Mapping(target = "paymentId", source = "id")
    ActualDetailListRsp entityToActualDetailListRsp(PaymentBaseInfo payment);

    PaymentBaseInfoLib entity2Lib(PaymentBaseInfo f);

    PaymentBaseInfo lib2Entity(PaymentBaseInfoLib t);

    @Mapping(target = "writeOffUserIds", source = "writeOffUserIds", qualifiedByName = "jsonStringToLongList")
    PaymentDetailRsp lib2DetailRsp(PaymentBaseInfoLib f);

    @Mapping(source = "pknumber", target = "flowId")
    PaymentActualDetail third2PaymentActual(ThirdPaymentDetailREQ req);

    PaymentPolicyExcelModel base2PaymentPolicyExport(PaymentPolicyInfo rew);
}
