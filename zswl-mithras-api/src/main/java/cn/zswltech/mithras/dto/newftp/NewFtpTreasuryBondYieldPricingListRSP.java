package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 10年期国债收益率定价
 * @date 2023-05-21
 */
@Data
@ApiModel("10年期国债收益率定价列表-返回体")
public class NewFtpTreasuryBondYieldPricingListRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "月份")
    private LocalDate month;

    @ApiModelProperty(value = "波动幅度")
    private Integer fluctuationRange;

    @ApiModelProperty(value = "ftp计价")
    private Integer ftpPricing;

}
