package cn.zswltech.mithras.blackgray.external.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 风险画像-集中度表格 查询
 * @Author:fengming.dai
 */
@Data
public class ImageTableQry {

//    @NotBlank(message = "名称必填")
    @ApiModelProperty("客户名称 ")
    String name;

//    @NotBlank(message = "集团名称")
    @ApiModelProperty("集团名称 ")
    String groupName;

    @NotBlank(message = "数据时点必填")
    @ApiModelProperty("数据时点 例如：2022-02")
    String timePoint;

    @NotNull(message = "type 必填")
    @ApiModelProperty("1 查单一客户，2查 集团 ")
    Integer type;
}
