package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @description 季度指导基础定价
 * @author zhaozhengkang
 * @date 2023-05-21
 */
@Data
@ApiModel("季度指导基础定价编辑-请求体")
public class NewFtpQuarterlyBasePricingModifyREQ {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "利率值")
    private Integer value;


}
