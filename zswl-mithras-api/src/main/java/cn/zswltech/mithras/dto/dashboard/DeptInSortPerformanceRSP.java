package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author yangxiong
 * @date 2024/6/24/15:29
 * @description
 */
@Data
@ApiModel(value = "部门内业绩排名响应体")
@EqualsAndHashCode(callSuper = true)
public class DeptInSortPerformanceRSP extends DeptSortPerformanceDTO {

    @ApiModelProperty(value = "项目经理ID")
    private Long projManagerId;

    @ApiModelProperty(value = "项目经理名称")
    private String projManagerName;
}
