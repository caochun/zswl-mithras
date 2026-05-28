package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2025/5/22
 * @description
 */
@Data
public class BudgetPlanProfitSummaryOtherRSP {
    @ApiModelProperty("年份")
    private String year;

    @ApiModelProperty("指标名称")
    private String metricName;

    @ApiModelProperty("部门数据")
    private List<DeptData> deptDataList;

    @Data
    public static class DeptData {
        @ApiModelProperty("部门id")
        private Long belongDeptId;

        @ApiModelProperty("部门名称")
        private String belongDeptName;

        @ApiModelProperty("公用事业")
        private Long publicUtilities = 0L;

        @ApiModelProperty("民生消费")
        private Long civilConsumption = 0L;

        @ApiModelProperty("国有产业")
        private Long stateOwnedIndustry = 0L;

        @ApiModelProperty("其他产业")
        private Long otherIndustry = 0L;

        @ApiModelProperty("部门小计")
        private Long sum = 0L;
    }
}
