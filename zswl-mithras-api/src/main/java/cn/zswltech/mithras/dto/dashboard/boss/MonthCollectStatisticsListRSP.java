package cn.zswltech.mithras.dto.dashboard.boss;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author bigbear
 * @date 2024/10/22 14:35
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "业务月度收入统计api")
public class MonthCollectStatisticsListRSP {

    @ApiModelProperty(value = "收入月份 format：yyyy-MM")
    private String incomeMonth;

    @ApiModelProperty(value = "累计收入完成率 n%")
    private ValueUnitDTO incomeCompletionRate;

    @ApiModelProperty(value = "收入金额（毫厘）")
    private ValueUnitDTO incomeTarget;

    @ApiModelProperty(value = "收入统计列表")
    private List<MonthCollectStatisticsDetail> monthCollectStatisticsDetailList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthCollectStatisticsDetail {

        @ApiModelProperty(value = "部门ID")
        private Long deptId;

        @ApiModelProperty(value = "部门名称")
        private String deptName;

        @ApiModelProperty(value = "收入目标（毫厘）")
        private ValueUnitDTO incomeAmount;

    }
}
