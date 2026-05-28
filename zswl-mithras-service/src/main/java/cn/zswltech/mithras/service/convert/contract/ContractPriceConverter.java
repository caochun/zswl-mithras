package cn.zswltech.mithras.service.convert.contract;

import cn.zswltech.mithras.dto.contract.price.*;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewAocPriceRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewFactoringPriceRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewLeasePriceRSP;
import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import cn.zswltech.mithras.service.mapper.model.contract.ContractAocPrice;
import cn.zswltech.mithras.service.mapper.model.contract.ContractFactoringPrice;
import cn.zswltech.mithras.service.mapper.model.contract.ContractLeasePrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/8 15:52
 */
@Mapper(uses = TypeConversionWorker.class, componentModel = "spring")
public interface ContractPriceConverter {

    @Mapping(source = "structuredInterestList", target = "structuredInterest", qualifiedByName = "toJsonString")
    ContractLeasePrice leaseModifyReqToEntity(ContractLeasePriceModifyREQ req);

    @Mapping(source = "structuredInterestList", target = "structuredInterest", qualifiedByName = "toJsonString")
    ContractAocPrice aocModifyReqToEntity(ContractAocPriceModifyREQ req);

    @Mapping(source = "structuredInterestList", target = "structuredInterest", qualifiedByName = "toJsonString")
    ContractFactoringPrice factoringModifyReqToEntity(ContractFactoringPriceModifyREQ req);


    ContractLeasePrice esLeaseRspToEntity(ProjReviewLeasePriceRSP rsp);

    @Mapping(source = "rentalCalcType", target = "repayCalcType")
    @Mapping(source = "applyCreditAmount", target = "contractAmount")
    ContractAocPrice esAocRspToEntity(ProjReviewAocPriceRSP rsp);

    @Mapping(source = "rentalCalcType", target = "repayCalcType")
    @Mapping(source = "applyCreditAmount", target = "contractAmount")
    ContractFactoringPrice esFactoringRspToEntity(ProjReviewFactoringPriceRSP rsp);

    ContractAocPriceDetailRSP entityToAocRsp(ContractAocPrice entity);
    ContractLeasePriceDetailRSP entityToLeaseRsp(ContractLeasePrice entity);
    ContractFactoringPriceDetailRSP entityToFactoringRsp(ContractFactoringPrice entity);


}
