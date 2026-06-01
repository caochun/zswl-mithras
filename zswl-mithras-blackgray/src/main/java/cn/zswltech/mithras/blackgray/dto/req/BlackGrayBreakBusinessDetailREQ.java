package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 黑灰名单突破申请
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单突破申请详情-fan体")
public class BlackGrayBreakBusinessDetailREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
