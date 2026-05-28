package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DashboardProjectInfoStatisticsRSP {
    @ApiModelProperty("分组名称code")
    private String groupCode;
    @ApiModelProperty("分组名称")
    private String group;
    @ApiModelProperty("数量")
    private Integer quantity;
    @ApiModelProperty("金额")
    private ValueUnitDTO amount;
    @ApiModelProperty("本月新增数量")
    private Integer incrementThisMonth;

    public DashboardProjectInfoStatisticsRSP(String groupCode, String group, Integer quantity, ValueUnitDTO amount) {
        this(groupCode, group, quantity, amount, null);
    }
}
