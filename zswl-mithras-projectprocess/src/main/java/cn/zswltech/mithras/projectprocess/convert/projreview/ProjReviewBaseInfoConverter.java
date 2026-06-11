package cn.zswltech.mithras.projectprocess.convert.projreview;

import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoListREQ;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoListRSP;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoModifyREQ;
import cn.zswltech.mithras.projectprocess.convert.ProjectProcessTypeConversionWorker;
import cn.zswltech.mithras.projectprocess.dto.persistence.ProjReviewListSelectDTO;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingBaseInfoLib;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
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
public interface ProjReviewBaseInfoConverter {

    ProjReviewBaseInfoConverter INSTANCE = Mappers.getMapper(ProjReviewBaseInfoConverter.class);

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
    @Mapping(source = "evaluationSubjectRatingScoreId", target = "ratingClientId")
    @Mapping(source = "evaluationSubjectRatingScore", target = "ratingFinalScore")
    @Mapping(source = "mainLesseeRatingScoreId", target = "ratingMainClientId")
    @Mapping(source = "mainLesseeRatingScore", target = "ratingMainFinalScore")
    ProjReviewBaseInfoDetailRSP entityToDetailRSP(ProjReviewBaseInfo baseInfo);

    @Mapping(source = "projCosponsorUserIds", target = "projCosponsorUserIds", qualifiedByName = "jsonStringToLongList")
    ProjReviewBaseInfoListRSP entityToListRsp(ProjReviewBaseInfo baseInfo);

    List<ProjReviewBaseInfoDetailRSP> entitysToDetailRSPs(List<ProjReviewBaseInfo> baseInfos);

    ProjReviewBaseInfo pricingLibToDetailReview(ProjPricingBaseInfoLib baseInfos);


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
    ProjReviewBaseInfo modifyREQtoEntity(ProjReviewBaseInfoModifyREQ modifyREQ);

    @Mapping(source = "createFrom", target = "createFrom", qualifiedByName = "startOfDay")
    @Mapping(source = "createTo", target = "createTo", qualifiedByName = "endOfDay")
    @Mapping(source = "updateFrom", target = "updateFrom", qualifiedByName = "startOfDay")
    @Mapping(source = "updateTo", target = "updateTo", qualifiedByName = "endOfDay")
    ProjReviewListSelectDTO listREQtoSelectDTO(ProjReviewBaseInfoListREQ listREQ);
}
