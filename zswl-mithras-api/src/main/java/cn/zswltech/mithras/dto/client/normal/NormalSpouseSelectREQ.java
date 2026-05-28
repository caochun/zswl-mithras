package cn.zswltech.mithras.dto.client.normal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author junke
 */
@Data
@ApiModel("配偶下拉-请求体")
public class NormalSpouseSelectREQ {

    @NotNull
    @ApiModelProperty("当前客户id")
    private Long clientId;
    @ApiModelProperty("模糊匹配名称")
    private String fuzzyName;
}
