package cn.zswltech.mithras.dto.dashboard.boss;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/15/17:42
 * @description
 */
@Data
public class OperationEfficiencyDetailListRSP {

    @ApiModelProperty(value = "部门名称")
    private String deptName;

    @ApiModelProperty(value = "统计明细")
    private List<OperationEfficiencyStatisticsListRSP> detail;
}
