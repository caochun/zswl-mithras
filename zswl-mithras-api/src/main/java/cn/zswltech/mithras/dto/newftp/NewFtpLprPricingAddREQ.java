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
@ApiModel("LPR定价新增-请求体")
public class NewFtpLprPricingAddREQ {

    /**
    * month
    */
    @ApiModelProperty(value = "month")
    private LocalDate month;

    @ApiModelProperty(value = "lprOneYear")
    private Integer lprOneYear;

    @ApiModelProperty(value = "lprOneYear")
    private Integer lprFiveYear;


}
