package cn.zswltech.mithras.ftp.oldftp.convert;

import cn.zswltech.mithras.dto.ftp.*;
import cn.zswltech.mithras.ftp.oldftp.model.*;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/9 16:47
 */
@Mapper(componentModel = "spring")
public interface FtpQuarterlyGuidanceConverter {
    FtpQuarterlyGuidance addReq2Entity(FtpQuarterlyGuidanceAddReq req);
    FtpQuarterlyGuidanceListRsp entity2ListRsp(FtpQuarterlyGuidance entity);
    List<FtpQuarterlyBasePricingRsp> baseEntity2Rsp(List<FtpQuarterlyBasePricing> entities);
    List<FtpQuarterlyCustomerPrincipalPricingRsp> customerEntity2Rsp(List<FtpQuarterlyCustomerPrincipalPricing> entities);
    List<FtpQuarterlyMonthPricingRsp> monthEntity2Rsp(List<FtpQuarterlyMonthPricing> entities);
    List<FtpQuarterlyEnterprisePricingRsp> enterpriseEntity2Rsp(List<FtpQuarterlyEnterprisePricing> entities);
    List<FtpQuarterlyBasePricingRsp> baseLib2Rsp(List<FtpQuarterlyBasePricingLib> basePricingLibs);
    FtpQuarterlyBasePricingRsp baseLib2Rsp(FtpQuarterlyBasePricingLib lib);
    List<FtpQuarterlyMonthPricingRsp> monthLib2Rsp(List<FtpQuarterlyMonthPricingLib> monthPricingLibs);
    FtpQuarterlyMonthPricingRsp monthLib2Rsp(FtpQuarterlyMonthPricingLib lib);
    List<FtpQuarterlyCustomerPrincipalPricingRsp> customerLib2Rsp(List<FtpQuarterlyCustomerPrincipalPricingLib> customerPricingLibs);
    FtpQuarterlyCustomerPrincipalPricingRsp customerLib2Rsp(FtpQuarterlyCustomerPrincipalPricingLib lib);

    List<FtpQuarterlyEnterprisePricingRsp> enterpriseLib2Rsp(List<FtpQuarterlyEnterprisePricingLib> enterprisePricingLibs);
    FtpQuarterlyEnterprisePricingRsp enterpriseLib2Rsp(FtpQuarterlyEnterprisePricingLib lib);
    FtpQuarterlyGuidanceDetailRsp entity2DetailRsp(FtpQuarterlyGuidance byId);
}
