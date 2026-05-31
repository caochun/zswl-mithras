package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description black_gray_break_business
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("black_gray_break_business删除-请求体")
public class BlackGrayBreakBusinessRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
