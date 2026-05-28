package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description LPR定价
 * @author zhaozhengkang
 * @date 2023-05-21
 */
@Data
@ApiModel("LPR定价编辑-请求体")
public class NewFtpLprPricingModifyREQ {

    /**
     * month
     */
    @ApiModelProperty(value = "month")
    private LocalDate month;

    /**
     * 一年期lpr
     */
    @ApiModelProperty(value = "lprOneYear")
    private Integer lprOneYear;

    /**
     * 五年期lpr
     */
    @ApiModelProperty(value = "lprOneYear")
    private Integer lprFiveYear;

}
