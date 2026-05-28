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
@ApiModel("首页工作台-超链接列表-请求体")
public class WorkbenchHyperlinkListReq {
    @ApiModelProperty("当前用户id")
    private Long userId;
}
