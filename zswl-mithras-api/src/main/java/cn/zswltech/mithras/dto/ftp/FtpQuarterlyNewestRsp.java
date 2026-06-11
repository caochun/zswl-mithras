package cn.zswltech.mithras.dto.ftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/13 16:11
 */
@Data
@ApiModel("季度最新版本数据返回体-返回体")
public class FtpQuarterlyNewestRsp {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("基础报价")
    private List<FtpQuarterlyBasePricingRsp> basePricing;
    @ApiModelProperty("客户主体计价")
    private List<FtpQuarterlyCustomerPrincipalPricingRsp> customerPricing;
    @ApiModelProperty("按企业性质报价")
    private List<FtpQuarterlyEnterprisePricingRsp> enterprisePricing;
    @ApiModelProperty("按月报价")
    private List<FtpQuarterlyMonthPricingRsp> monthPricing;

}
