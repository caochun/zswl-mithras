package cn.zswltech.mithras.projectprocess.convert.projreview;

import cn.zswltech.mithras.dto.projestablish.priceaoc.ProjEstablishAocPriceRSP;
import cn.zswltech.mithras.dto.projestablish.pricefactoring.ProjEstablishFactoringPriceRSP;
import cn.zswltech.mithras.dto.projestablish.pricelease.ProjEstablishLeasePriceRSP;
import cn.zswltech.mithras.dto.projreview.price.*;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingAocPrice;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingFactoringPrice;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingLeasePrice;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewAocPrice;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewFactoringPrice;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewLeasePrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/8 15:52
 */
@Mapper(componentModel = "spring")
public interface ProjReviewPriceConverter {

    ProjReviewAocPrice aocModifyReqToEntity(ProjReviewAocPriceModifyREQ req);
    ProjReviewLeasePrice leaseModifyReqToEntity(ProjReviewLeasePriceModifyREQ req);
    ProjReviewFactoringPrice factoringModifyReqToEntity(ProjReviewFactoringPriceModifyREQ req);

    @Mapping(source = "projectApprovalAmount", target = "approvedAmount")
    ProjReviewAocPriceRSP entityToAocRsp(ProjReviewAocPrice entity);
    @Mapping(source = "projectApprovalAmount", target = "approvedAmount")
    ProjReviewLeasePriceRSP entityToLeaseRsp(ProjReviewLeasePrice entity);
    @Mapping(source = "projectApprovalAmount", target = "approvedAmount")
    ProjReviewFactoringPriceRSP entityToFactoringRsp(ProjReviewFactoringPrice entity);

    ProjReviewAocPrice esAocRspToEntity(ProjEstablishAocPriceRSP rsp);
    ProjReviewLeasePrice esLeaseRspToEntity(ProjEstablishLeasePriceRSP rsp);
    ProjReviewFactoringPrice esFactoringRspToEntity(ProjEstablishFactoringPriceRSP rsp);

    ProjReviewAocPrice reviewAocRspToPricingEntity(ProjPricingAocPrice rsp);
    ProjReviewLeasePrice reviewLeaseRspToPricingEntity(ProjPricingLeasePrice rsp);
    ProjReviewFactoringPrice reviewFactoringRspToPricingEntity(ProjPricingFactoringPrice rsp);

}
