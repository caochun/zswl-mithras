package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 一年期shibor利率定价
 * @date 2023-05-21
 */
@Data
@ApiModel("一年期shibor利率定价列表-返回体")
public class NewFtpShiborInterestRatePricingListRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "月份")
    private LocalDate month;

    @ApiModelProperty(value = "ftp定价")
    private Integer ftpPricing;

}
