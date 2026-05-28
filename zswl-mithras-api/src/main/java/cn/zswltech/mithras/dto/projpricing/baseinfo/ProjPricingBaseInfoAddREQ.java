package cn.zswltech.mithras.dto.projpricing.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@ApiModel("项目定价基本信息表新增-请求体")
public class ProjPricingBaseInfoAddREQ {

    @ApiModelProperty(value = "立项ID")
    @NotNull(message = "不能为空")
    private Long projEstablishId;

    @ApiModelProperty(value = "项目名称")
    private String projName;

    @ApiModelProperty(value = "客户Name")
    private String clientName;

    @ApiModelProperty(value = "业务类型")
    private String bizType;
}
