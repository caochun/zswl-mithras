package cn.zswltech.mithras.projectprocess.convert.projpricing;

import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoListREQ;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoListRSP;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoModifyREQ;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.projectprocess.convert.ProjectProcessTypeConversionWorker;
import cn.zswltech.mithras.projectprocess.mapper.dto.ProjPricingListSelectDTO;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfoLib;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/5 17:11
 */
@Mapper(uses = ProjectProcessTypeConversionWorker.class, componentModel = "spring")
public interface ProjPricingBaseInfoConverter {

    ProjPricingBaseInfoConverter INSTANCE = Mappers.getMapper(ProjPricingBaseInfoConverter.class);

    @Mapping(source = "leaseTypes", target = "leaseTypes", qualifiedByName = "jsonStringToStringList")
    @Mapping(source = "factoringTypes", target = "factoringTypes", qualifiedByName = "jsonStringToStringList")
    @Mapping(source = "zrTypes", target = "zrTypes", qualifiedByName = "jsonStringToStringList")
    @Mapping(source = "lesseeInfo", target = "lesseeInfo", qualifiedByName = "jsonStringToClientInfoList")
    @Mapping(source = "creditorInfo", target = "creditorInfo", qualifiedByName = "jsonStringToClientInfoList")
    @Mapping(source = "debtorInfo", target = "debtorInfo", qualifiedByName = "jsonStringToClientInfoList")
    @Mapping(source = "guaranteeInfo", target = "guaranteeInfo", qualifiedByName = "jsonStringToClientInfoList")
    @Mapping(source = "pledgorInfo", target = "pledgorInfo", qualifiedByName = "jsonStringToClientInfoList")
    @Mapping(source = "mortgagorInfo", target = "mortgagorInfo", qualifiedByName = "jsonStringToClientInfoList")
    @Mapping(source = "projCosponsorUserIds", target = "projCosponsorUserIds", qualifiedByName = "jsonStringToLongList")
    ProjPricingBaseInfoDetailRSP entityToDetailRSP(ProjPricingBaseInfo baseInfo);

    @Mapping(source = "projCosponsorUserIds", target = "projCosponsorUserIds", qualifiedByName = "jsonStringToLongList")
    ProjPricingBaseInfoListRSP entityToListRsp(ProjPricingBaseInfo baseInfo);

    List<ProjPricingBaseInfoDetailRSP> entitysToDetailRSPs(List<ProjPricingBaseInfo> baseInfos);

    ProjPricingBaseInfo reviewLibToBasePricing(ProjReviewBaseInfoLib baseInfos);



    @Mapping(source = "leaseTypes", target = "leaseTypes", qualifiedByName = "toJsonString")
    @Mapping(source = "factoringTypes", target = "factoringTypes", qualifiedByName = "toJsonString")
    @Mapping(source = "zrTypes", target = "zrTypes", qualifiedByName = "toJsonString")
    @Mapping(source = "lesseeInfo", target = "lesseeInfo", qualifiedByName = "toJsonString")
    @Mapping(source = "creditorInfo", target = "creditorInfo", qualifiedByName = "toJsonString")
    @Mapping(source = "debtorInfo", target = "debtorInfo", qualifiedByName = "toJsonString")
    @Mapping(source = "guaranteeInfo", target = "guaranteeInfo", qualifiedByName = "toJsonString")
    @Mapping(source = "pledgorInfo", target = "pledgorInfo", qualifiedByName = "toJsonString")
    @Mapping(source = "mortgagorInfo", target = "mortgagorInfo", qualifiedByName = "toJsonString")
    @Mapping(source = "projCosponsorUserIds", target = "projCosponsorUserIds", qualifiedByName = "toJsonString")
    ProjPricingBaseInfo modifyREQtoEntity(ProjPricingBaseInfoModifyREQ modifyREQ);

    @Mapping(source = "createFrom", target = "createFrom", qualifiedByName = "startOfDay")
    @Mapping(source = "createTo", target = "createTo", qualifiedByName = "endOfDay")
    @Mapping(source = "updateFrom", target = "updateFrom", qualifiedByName = "startOfDay")
    @Mapping(source = "updateTo", target = "updateTo", qualifiedByName = "endOfDay")
    ProjPricingListSelectDTO listREQtoSelectDTO(ProjPricingBaseInfoListREQ listREQ);
}
