package cn.zswltech.mithras.service.convert.ftp;

import cn.zswltech.mithras.dto.ftp.*;
import cn.zswltech.mithras.ftp.model.*;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/10 14:05
 */
@Mapper(componentModel = "spring")
public interface FtpMonthlyGuidanceConverter {
    FtpMonthlyGuidance addReq2Entity(FtpMonthlyGuidanceAddReq req);

    FtpMonthlyGuidanceListRsp entity2ListRsp(FtpMonthlyGuidance guidance);

    FtpMonthlyGuidanceDetailRsp entity2DetailRsp(FtpMonthlyGuidance byId);

    List<FtpMonthlyValuationRsp> valuationEntity2Rsp(List<FtpMonthlyValuation> selectByGuidanceId);

    List<FtpMonthlyPricingRsp> pricingEntity2Rsp(List<FtpMonthlyPricing> selectByGuidanceId);

    FtpMonthlyPricingRsp pricingLib2Rsp(FtpMonthlyPricingLib pricingLibs);

    FtpMonthlyValuationRsp valuationLib2Rsp(FtpMonthlyValuationLib val);

    FtpMonthlyGuidanceDetailRsp lib2DetailRsp(FtpMonthlyGuidanceLib guidanceLib);
}
