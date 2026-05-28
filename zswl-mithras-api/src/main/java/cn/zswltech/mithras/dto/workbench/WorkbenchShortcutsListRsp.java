package cn.zswltech.mithras.dto.workbench;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jackerHe
 * @description 首页工作台-公告
 * @date 2023-03-15
 */
@Data
@ApiModel("首页工作台-公告新增-返回体")
public class WorkbenchShortcutsListRsp {

    @ApiModelProperty(value = "路径")
    private String path;

    @ApiModelProperty(value = "菜单id")
    private Long menuId;

    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    @ApiModelProperty(value = "常用功能标识 1常用，0 其他")
    private Integer isCollect;

    @ApiModelProperty(value = "提示")
    private String message;

    @ApiModelProperty(value = "icon")
    private String icon;

}
