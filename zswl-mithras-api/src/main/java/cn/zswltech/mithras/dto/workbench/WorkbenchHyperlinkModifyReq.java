package cn.zswltech.mithras.dto.workbench;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhaozhengkang
 * @description 首页工作台-超链接
 * @date 2023-03-17
 */
@Data
@ApiModel("首页工作台-超链接编辑-请求体")
public class WorkbenchHyperlinkModifyReq {
    @ApiModelProperty(value = "用户id")
    private Long userId;
    @ApiModelProperty("批量更新请求全体")
    List<WorkbenchHyperlinkDto> modifyReq;
}
