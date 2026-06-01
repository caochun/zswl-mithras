package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 黑灰名单库
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单库删除-请求体")
public class BlackGrayLibraryRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
