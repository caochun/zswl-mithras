package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junke
 */
@Data
@ApiModel("判断客户名称是否存在-请求参数")
public class ClientNameExistREQ {

    @ApiModelProperty("客户名称")
    private String clientName;
}
