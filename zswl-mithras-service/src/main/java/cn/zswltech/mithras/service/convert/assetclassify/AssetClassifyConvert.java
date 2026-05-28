package cn.zswltech.mithras.service.convert.assetclassify;

import cn.zswltech.mithras.dto.assetclassify.AssetClassifyClientDetailRSP;
import cn.zswltech.mithras.dto.assetclassify.QuarterDetailRSP;
import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassify;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyClient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @ClassName AssetClassifyConvert
 * @Author jackerhe
 * @Date 2023/1/4 2:17 下午
 * @Version 1.0
 **/
@Mapper(uses = TypeConversionWorker.class, componentModel = "spring")
public interface AssetClassifyConvert {

    @Mapping(source = "classifyAmount", target = "classificationAmounts", qualifiedByName = "jsonStringToObject")
    QuarterDetailRSP assetClassify2QuarterDetailRSP(AssetClassify assetClassify);

    @Mapping(source = "startRentContractCodes", target = "startRentContractCodes", qualifiedByName = "jsonStringToObject")
    @Mapping(source = "startRentContractRemainingPrincipal", target = "startRentContractRemainingPrincipal", qualifiedByName = "jsonStringToLongList")
    AssetClassifyClientDetailRSP assetClassifyClient2AssetClassifyClientDetailRSP(AssetClassifyClient assetClassifyClient);
}
