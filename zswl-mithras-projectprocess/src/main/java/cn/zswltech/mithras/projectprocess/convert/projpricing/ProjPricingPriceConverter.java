package cn.zswltech.mithras.projectprocess.convert.projpricing;

import cn.zswltech.mithras.dto.projestablish.priceaoc.ProjEstablishAocPriceRSP;
import cn.zswltech.mithras.dto.projestablish.pricefactoring.ProjEstablishFactoringPriceRSP;
import cn.zswltech.mithras.dto.projestablish.pricelease.ProjEstablishLeasePriceRSP;
import cn.zswltech.mithras.dto.projpricing.price.*;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingAocPrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingFactoringPrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingLeasePrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewAocPrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewFactoringPrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewLeasePrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/8 15:52
 */
@Mapper(componentModel = "spring")
public interface ProjPricingPriceConverter {

    @Mapping(source = "approvedAmount", target = "projectApprovalAmount")
    ProjPricingAocPrice aocModifyReqToEntity(ProjPricingAocPriceModifyREQ req);
    @Mapping(source = "approvedAmount", target = "projectApprovalAmount")
    ProjPricingLeasePrice leaseModifyReqToEntity(ProjPricingLeasePriceModifyREQ req);
    @Mapping(source = "approvedAmount", target = "projectApprovalAmount")
    ProjPricingFactoringPrice factoringModifyReqToEntity(ProjPricingFactoringPriceModifyREQ req);

    @Mapping(source = "projectApprovalAmount", target = "approvedAmount")
    ProjPricingAocPriceRSP entityToAocRsp(ProjPricingAocPrice entity);

    @Mapping(source = "projectApprovalAmount", target = "approvedAmount")
    ProjPricingLeasePriceRSP entityToLeaseRsp(ProjPricingLeasePrice entity);

    @Mapping(source = "projectApprovalAmount", target = "approvedAmount")
    ProjPricingFactoringPriceRSP entityToFactoringRsp(ProjPricingFactoringPrice entity);

    ProjPricingAocPrice esAocRspToEntity(ProjEstablishAocPriceRSP rsp);
    ProjPricingLeasePrice esLeaseRspToEntity(ProjEstablishLeasePriceRSP rsp);
    ProjPricingFactoringPrice esFactoringRspToEntity(ProjEstablishFactoringPriceRSP rsp);

    ProjPricingAocPrice reviewAocRspToPricingEntity(ProjReviewAocPrice rsp);
    ProjPricingLeasePrice reviewLeaseRspToPricingEntity(ProjReviewLeasePrice rsp);
    ProjPricingFactoringPrice reviewFactoringRspToPricingEntity(ProjReviewFactoringPrice rsp);

}
