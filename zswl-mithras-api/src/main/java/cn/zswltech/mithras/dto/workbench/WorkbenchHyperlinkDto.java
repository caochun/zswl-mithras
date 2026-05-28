package cn.zswltech.mithras.dto.workbench;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 首页工作台-超链接
 * @date 2023-03-17
 */
@Data
@ApiModel("首页工作台-超链接列表-返回体")
public class WorkbenchHyperlinkDto {

    @ApiModelProperty(value = "id")
    private Long id;
    
    @ApiModelProperty(value = "name")
    private String name;
    @ApiModelProperty(value = "address")
    private String address;

    @ApiModelProperty(value = "用户id")
    private Long userId;
}
