package cn.zswltech.mithras.dto.client.normal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junke
 */
@ApiModel("自热人基本信息修改-请求体")
@Data
public class NormalBaseInfoModifyREQ extends NormalBaseInfoAddREQ {

    @ApiModelProperty(value = "客户名称", required = true)
    private String clientName;

    @ApiModelProperty(value = "客户代码", required = false)
    private String clientCode;

}
