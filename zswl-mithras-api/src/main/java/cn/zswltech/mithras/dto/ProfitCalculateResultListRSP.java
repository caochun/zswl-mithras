package cn.zswltech.mithras.dto;

import cn.zswltech.mithras.api.common.PageR;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/6/25
 * @description
 */
@Data
@ApiModel("小工具-会计利润测算-分页列表-返回参数")
public class ProfitCalculateResultListRSP {
    @ApiModelProperty("测算日期")
    private String calculateDate;

    @ApiModelProperty("分页结果对象")
    private PageR<Data> pageResult;

    @lombok.Data
    public static class Data {
        @ApiModelProperty("主键id")
        private Long id;

        @ApiModelProperty("客户id")
        private Long clientId;

        @ApiModelProperty("客户名称")
        private String clientName;

        @ApiModelProperty("项目类型")
        private String projectClassify;

        @ApiModelProperty("合同id")
        private Long contractId;

        @ApiModelProperty("合同编号")
        private String contractCode;

        @ApiModelProperty("业务部门id")
        private Long bizDeptId;

        @ApiModelProperty("业务部门名称")
        private String bizDeptName;

        @ApiModelProperty("业务类型")
        private String bizType;

        @ApiModelProperty("投放时间")
        private String contractStartDate;

        @ApiModelProperty("当年已确认收入（税后）")
        private Long confirmIncomeThisYear;

        @ApiModelProperty("当年测算利息收入")
        private Long calculateInterestThisYear;

        @ApiModelProperty("营业收入")
        private Long operatingIncome;

        @ApiModelProperty("FTP成本")
        private Long ftpInterest;

        @ApiModelProperty("上期末风险金余额")
        private Long riskBalanceEndOfLastYear;

        @ApiModelProperty("本期末风险金余额")
        private Long riskBalanceEndOfThisYear;

        @ApiModelProperty("本年风险金计提/转回")
        private Long riskUsedThisYear;

        @ApiModelProperty("附加税")
        private Long additionalTax;

        @ApiModelProperty("利润总额")
        private Long profit;

        @ApiModelProperty("利润总额（扣除费用）")
        private Long profitExcludeFee;

        @ApiModelProperty("本年末剩余本金")
        private Long remainingPrincipleEndOfThisYear;

        @ApiModelProperty("本年末保证金余额")
        private Long remainingEarnestEndOfThisYear;

        @ApiModelProperty("年末敞口")
        private Long riskExposureEndOfThisYear;
    }
}
