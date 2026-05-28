package cn.zswltech.mithras.dto.budget;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 预算管理-预算计划-成本预算-明细
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-预算计划-成本预算-明细列表-返回体")
public class BudgetPlanCostDetailListRSP {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
    * 逻辑删除，0-未删除
    */
    @ApiModelProperty(value = "逻辑删除，0-未删除")
    private Integer deleted;

    /**
    * 预算计划id
    */
    @ApiModelProperty(value = "预算计划id")
    private Long budgetPlanId;

    /**
    * 成本预算id
    */
    @ApiModelProperty(value = "成本预算id")
    private Long budgetPlanCostId;

    /**
    * 投放计划详情id
    */
    @ApiModelProperty(value = "投放计划详情id")
    private Long budgetPlanPayDetailId;

    /**
    * 年份
    */
    @ApiModelProperty(value = "年份")
    private Integer year;

    /**
    * 月份
    */
    @ApiModelProperty(value = "月份")
    private Integer month;

//    /**
//    * 所属部门id
//    */
//    @ApiModelProperty(value = "所属部门id")
//    private Long belongDeptId;
//
//    /**
//    * 项目评审id
//    */
//    @ApiModelProperty(value = "项目评审id")
//    private Long projReviewId;
//
//    /**
//    * 合同id
//    */
//    @ApiModelProperty(value = "合同id")
//    private Long contractId;
//
//    /**
//    * 借据id
//    */
//    @ApiModelProperty(value = "借据id")
//    private Long receiptId;

    /**
    * 存量租金回笼
    */
    @ApiModelProperty(value = "存量租金回笼")
    private Long projectRentHistory = 0L;

    /**
    * 存量本金回笼
    */
    @ApiModelProperty(value = "存量本金回笼")
    private Long projectPrincipalHistory = 0L;

    /**
    * 存量利息回笼
    */
    @ApiModelProperty(value = "存量利息回笼")
    private Long projectInterestHistory = 0L;

    /**
    * 还贷存量
    */
    @ApiModelProperty(value = "还贷存量")
    private Long financeRepayHistory = 0L;

    /**
    * 归还存量借款本金
    */
    @ApiModelProperty(value = "归还存量借款本金")
    private Long financePrincipalHistory = 0L;

    /**
    * 归还存量借款利息
    */
    @ApiModelProperty(value = "归还存量借款利息")
    private Long financeInterestHistory = 0L;

    /**
    * 退回保证金
    */
    @ApiModelProperty(value = "退回保证金")
    private Long projectDepositHistory = 0L;

    /**
    * 新增保证金
    */
    @ApiModelProperty(value = "新增保证金")
    private Long projectDepositFeature = 0L;

    /**
    * 新增服务费/咨询费
    */
    @ApiModelProperty(value = "新增服务费/咨询费")
    private Long projectConsultingFeeFeature = 0L;

    /**
    * 新增投放
    */
    @ApiModelProperty(value = "新增投放")
    private Long projectPayFeature = 0L;

    /**
    * 新增租金回笼
    */
    @ApiModelProperty(value = "新增租金回笼")
    private Long projectRentFeature = 0L;

    /**
    * 新增本金回笼
    */
    @ApiModelProperty(value = "新增本金回笼")
    private Long projectPrincipalFeature = 0L;

    /**
    * 新增利息回笼
    */
    @ApiModelProperty(value = "新增利息回笼")
    private Long projectInterestFeature = 0L;

    /**
    * 资金缺口
    */
    @ApiModelProperty(value = "资金缺口")
    private Long fundGap = 0L;

}
