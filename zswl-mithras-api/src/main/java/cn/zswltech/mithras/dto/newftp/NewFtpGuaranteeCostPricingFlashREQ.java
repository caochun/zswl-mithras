package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description 担保成本定价
 * @author zhaozhengkang
 * @date 2023-05-21
 */
@Data
@ApiModel("担保成本定价编辑刷新-请求体")
public class NewFtpGuaranteeCostPricingFlashREQ {


    /**
    * month
    */
    @ApiModelProperty(value = "month")
    private LocalDate month;


}
