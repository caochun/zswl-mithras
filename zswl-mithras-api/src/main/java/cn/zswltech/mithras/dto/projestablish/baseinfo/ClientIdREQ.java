package cn.zswltech.mithras.dto.projestablish.baseinfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author junke
 */
@Data
public class ClientIdREQ {
    
    @NotNull
    @ApiModelProperty("客户id")
    private Long clientId;
}
