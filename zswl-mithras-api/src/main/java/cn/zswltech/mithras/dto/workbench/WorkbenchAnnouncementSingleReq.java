package cn.zswltech.mithras.dto.workbench;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhaozhengkang
 * @description 首页工作台-公告
 * @date 2023-03-15
 */
@Data
@ApiModel("首页工作台-公告删除-请求体")
public class WorkbenchAnnouncementSingleReq {
    @NotNull
    @ApiModelProperty("id")
    private Long id;
}
