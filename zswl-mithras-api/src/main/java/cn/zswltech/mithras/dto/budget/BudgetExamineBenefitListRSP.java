package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @description 预算管理-预算考核-效益考核表
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-预算考核-效益考核表列表-返回体")
public class BudgetExamineBenefitListRSP {

    /**
     * 业务部门id
     */
    @ApiModelProperty(value = "业务部门id")
    private Long belongDeptId;

    /**
     * 预算考核id
     */
    private Long budgetExamineId;

    private Integer budgetExamineYear;

    private Integer budgetExamineMonth;

    @ApiModelProperty(value = "业务部门名称")
    private String belongDeptName;

    @ApiModelProperty(value = "本月数")
    private List<BudgetExamineBenefitBody> currentMonthList = Collections.emptyList();

    @ApiModelProperty(value = "本年累计")
    private List<BudgetExamineBenefitBody> currentYearList = Collections.emptyList();

    private int sort;

    @Data
    public static class BudgetExamineBenefitBody {

        @ApiModelProperty(value = "id")
        private Long id;
        /**
         * 字段名称
         */
        @ApiModelProperty(value = "字段名称")
        private String fieldName;

        /**
         * 字段值
         */
        @ApiModelProperty(value = "字段值")
        private Long fieldValue;

        /**
         * 字段层级
         */
        @ApiModelProperty(value = "字段层级")
        private Integer fieldLevel;
    }

}
