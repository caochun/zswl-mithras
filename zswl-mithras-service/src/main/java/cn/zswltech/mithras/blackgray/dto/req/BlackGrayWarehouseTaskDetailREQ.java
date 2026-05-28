package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 黑灰名单任务表
 * @author 
 * @date 2024-01-16
 */
@Data
@ApiModel("黑灰名单任务表详情-请求体")
public class BlackGrayWarehouseTaskDetailREQ {

    @ApiModelProperty(value = "任务id")
    @NotNull(message = "任务id不能为空")
    private Long id;
}
