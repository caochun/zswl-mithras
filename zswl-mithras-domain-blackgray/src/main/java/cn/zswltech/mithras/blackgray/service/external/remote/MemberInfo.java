package cn.zswltech.mithras.blackgray.service.external.remote;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author:fengming.dai
 */
@ApiModel
@Data
public class MemberInfo {

    Long id;

    @ApiModelProperty("成员名称")
    String name;

    @ApiModelProperty("企业经济成分")
    Integer economicComposition;

    @ApiModelProperty("地域")
    String area;

    @ApiModelProperty("所属行业")
    String industry;

    @ApiModelProperty("业务余额")
    BigDecimal bizRestAmount;

    @ApiModelProperty("风险暴露")
    BigDecimal riskExposure;

    @ApiModelProperty("不良余额")
    BigDecimal badBalance;
}
