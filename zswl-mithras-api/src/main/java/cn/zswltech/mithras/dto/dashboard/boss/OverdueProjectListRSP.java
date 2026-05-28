package cn.zswltech.mithras.dto.dashboard.boss;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangxiong
 * @date 2024/5/15/19:10
 * @description
 */
@Data
public class OverdueProjectListRSP {

    @ApiModelProperty(value = "项目ID")
    private Long projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "逾期金额")
    private ValueUnitDTO overdueAmount;

    @ApiModelProperty(value = "逾期天数")
    private ValueUnitDTO overdueDays;

    @ApiModelProperty(value = "所属部门")
    private String deptName;

    @ApiModelProperty(value = "所属主办")
    private String sponsorName;
}
