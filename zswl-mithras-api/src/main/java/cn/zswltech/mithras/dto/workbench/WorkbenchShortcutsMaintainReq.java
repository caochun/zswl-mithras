package cn.zswltech.mithras.dto.workbench;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author jackerhe
 * @description 首页工作台-公告
 * @date 2023-03-15
 */
@Data
@ApiModel("首页工作台-快捷功能-维护-请求体")
public class WorkbenchShortcutsMaintainReq{
    @ApiModelProperty("常用功能id")
    private List<Long> menuIds;
}
