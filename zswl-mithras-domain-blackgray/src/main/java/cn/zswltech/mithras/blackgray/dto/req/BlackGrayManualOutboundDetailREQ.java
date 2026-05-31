package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 黑灰名单人工出库表
 * @author
 * @date 2023-11-29
 */
@Data
@ApiModel("黑灰名单人工出库表详情-请求体")
public class BlackGrayManualOutboundDetailREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

    private Long auditTaskId;

}
