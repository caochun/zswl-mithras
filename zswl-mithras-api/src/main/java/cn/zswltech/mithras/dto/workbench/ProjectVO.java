package cn.zswltech.mithras.dto.workbench;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/10 14:15
 */
@Data
@ApiModel("工作台-项目指标列表-返回体")
public class ProjectVO {

    @ApiModelProperty("项目id")
    private Long projectId;

    @ApiModelProperty("项目名称")
    private String projectName;

    @ApiModelProperty("项目主办")
    private String projectOrganizer;

    @ApiModelProperty("项目协办")
    private String projectCoOrganizer;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("授信金额，万元，小数点后2位")
    private String applyCreditAmount;

    @ApiModelProperty("审批通过时间")
    private LocalDateTime approveTime;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

}
