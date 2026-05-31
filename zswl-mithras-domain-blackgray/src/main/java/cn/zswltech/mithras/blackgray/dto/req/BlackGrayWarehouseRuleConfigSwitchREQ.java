package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description 黑灰名单库-入库原因参数配置
 * @author 
 * @date 2024-01-18
 */
@Data
@ApiModel("黑灰名单库-入库原因参数配置删除-请求体")
public class BlackGrayWarehouseRuleConfigSwitchREQ {

    @ApiModelProperty(value = "状态 0启用，1禁用")
    private Integer status;

    @NotNull
    @ApiModelProperty("ids")
    private List<Long> ids;

}
