package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2025/5/14
 * @description
 */
@Data
public class BudgetPlanProfitProcessRSP {
    @ApiModelProperty("日期")
    private String date;

    @ApiModelProperty("部门数据")
    private List<DataGroupByDept> deptDataList;

    @Data
    public static class DataGroupByDept {
        @ApiModelProperty("部门id")
        private Long deptId;

        @ApiModelProperty("部门名称")
        private String deptName;

        @ApiModelProperty("公用事业")
        private Long publicUtilities = 0L;

        @ApiModelProperty("民生消费")
        private Long civilConsumption = 0L;

        @ApiModelProperty("国有产业")
        private Long stateOwnedIndustry = 0L;

        @ApiModelProperty("其他产业")
        private Long otherIndustry = 0L;

        public void publicUtilitiesAdd(long amount) {
            this.publicUtilities += amount;
        }

        public void civilConsumptionAdd(long amount) {
            this.civilConsumption += amount;
        }

        public void stateOwnedIndustryAdd(long amount) {
            this.stateOwnedIndustry += amount;
        }

        public void otherIndustryAdd(long amount) {
            this.otherIndustry += amount;
        }
    }
}
