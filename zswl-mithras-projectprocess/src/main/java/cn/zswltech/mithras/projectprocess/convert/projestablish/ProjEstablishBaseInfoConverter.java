package cn.zswltech.mithras.projectprocess.convert.projestablish;


import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoModifyREQ;
import cn.zswltech.mithras.projectprocess.convert.ProjectProcessTypeConversionWorker;
import cn.zswltech.mithras.projectprocess.dto.persistence.ProjEstablishListSelectDTO;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/5 17:11
 */
@Mapper(uses = ProjectProcessTypeConversionWorker.class, componentModel = "spring")
public interface ProjEstablishBaseInfoConverter {
    
    @Mapping(source = "leaseTypes", target = "leaseTypes", qualifiedByName = "jsonStringToStringList")
    @Mapping(source = "lesseeInfo", target = "lesseeInfo", qualifiedByName = "jsonStringToPersonInfoList")
    @Mapping(source = "creditorInfo", target = "creditorInfo", qualifiedByName = "jsonStringToPersonInfoList")
    @Mapping(source = "debtorInfo", target = "debtorInfo", qualifiedByName = "jsonStringToPersonInfoList")
    @Mapping(source = "guaranteeInfo", target = "guaranteeInfo", qualifiedByName = "jsonStringToPersonInfoList")
    @Mapping(source = "pledgorInfo", target = "pledgorInfo", qualifiedByName = "jsonStringToPersonInfoList")
    @Mapping(source = "mortgagorInfo", target = "mortgagorInfo", qualifiedByName = "jsonStringToPersonInfoList")
    @Mapping(source = "projCosponsorUserIds", target = "projCosponsorUserIds", qualifiedByName = "jsonStringToLongList")
    @Mapping(source = "riskControlManagerId", target = "riskControlManagerId", qualifiedByName = "jsonStringToLongList")
    @Mapping(source = "evaluationSubjectRatingScoreId", target = "ratingClientId")
    @Mapping(source = "evaluationSubjectRatingScore", target = "ratingFinalScore")
    @Mapping(source = "mainLesseeRatingScoreId", target = "ratingMainClientId")
    @Mapping(source = "mainLesseeRatingScore", target = "ratingMainFinalScore")
    ProjEstablishBaseInfoListRSP entityToDetailRSP(ProjEstablishBaseInfo baseInfo);

    @Mapping(source = "leaseTypes", target = "leaseTypes", qualifiedByName = "toJsonString")
    @Mapping(source = "lesseeInfo", target = "lesseeInfo", qualifiedByName = "toJsonString")
    @Mapping(source = "creditorInfo", target = "creditorInfo", qualifiedByName = "toJsonString")
    @Mapping(source = "debtorInfo", target = "debtorInfo", qualifiedByName = "toJsonString")
    @Mapping(source = "guaranteeInfo", target = "guaranteeInfo", qualifiedByName = "toJsonString")
    @Mapping(source = "pledgorInfo", target = "pledgorInfo", qualifiedByName = "toJsonString")
    @Mapping(source = "mortgagorInfo", target = "mortgagorInfo", qualifiedByName = "toJsonString")
    @Mapping(source = "projCosponsorUserIds", target = "projCosponsorUserIds", qualifiedByName = "toJsonString")
    ProjEstablishBaseInfo modifyREQtoEntity(ProjEstablishBaseInfoModifyREQ modifyREQ);

    @Mapping(source = "createFrom", target = "createFrom", qualifiedByName = "startOfDay")
    @Mapping(source = "createTo", target = "createTo", qualifiedByName = "startOfDay")
    @Mapping(source = "updateFrom", target = "updateFrom", qualifiedByName = "startOfDay")
    @Mapping(source = "updateTo", target = "updateTo", qualifiedByName = "startOfDay")
    ProjEstablishListSelectDTO listREQtoSelectDTO(ProjEstablishBaseInfoListREQ listREQ);

}
