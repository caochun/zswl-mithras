package cn.zswltech.mithras.afterlease.application.convert;

import cn.zswltech.mithras.dto.afterlease.AfterLeaseAdjustDetailRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseAdjustInfoListRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseAdjustInfoModifyREQ;
import cn.zswltech.mithras.afterlease.mapper.model.AfterLeaseAdjustInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @ClassName AfterLeaseAdjustConvert
 * @Description
 * @Author jackerhe
 * @Date 2022/11/8 10:52 上午
 * @Version 1.0
 **/
@Mapper(uses = AfterLeaseTypeConversionWorker.class, componentModel = "spring")
public interface AfterLeaseAdjustConvert {

    @Mapping(source = "id", target = "projId")
    AfterLeaseAdjustInfo projReviewBase2AfterLeaseAdjust(ProjReviewBaseInfo source);

    AfterLeaseAdjustInfo AdjustInfoModifyREQ2LeaseAdjust(AfterLeaseAdjustInfoModifyREQ req);

    @Mapping(source = "projCosponsorUserIds", target = "projCosponsorUserIds", qualifiedByName = "jsonStringToLongList")
    @Mapping(source = "createTime", target = "afterLeaseAdjustData")
    AfterLeaseAdjustInfoListRSP baseInfo2AdjustInfoListRSP(AfterLeaseAdjustInfo base);

    @Mapping(source = "projCosponsorUserIds", target = "projCosponsorUserIds", qualifiedByName = "jsonStringToLongList")
    AfterLeaseAdjustDetailRSP baseInfo2adjustDetailRSP(AfterLeaseAdjustInfo base);

}
