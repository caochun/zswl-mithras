package cn.zswltech.mithras.dto.workbench;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/10 14:16
 */
@Data
@ApiModel("工作台-项目指标列表-请求体")
public class ProjectMetricReq extends PageReq {
    @ApiModelProperty("业务部门id")
    private Long deptId;
    @ApiModelProperty("开始时间")
    private LocalDateTime startDateTime;
}
