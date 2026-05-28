package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/24
 * @description
 */
@Data
public class DashboardFundFinanceRepayRSP {
    @ApiModelProperty("还本付息ID")
    private Long receiptRepayCashFlowId;
    @ApiModelProperty("状态")
    private String writeOffState;
    @ApiModelProperty("状态display")
    private String writeOffStateDisplay;
    @ApiModelProperty("融资ID")
    private Long financingId;
    @ApiModelProperty("融资编号")
    private String financingCode;
    @ApiModelProperty("是否直融，0-否，1-是")
    private Integer isDirect;
    @ApiModelProperty("机构名称/产品名称")
    private String orgName;
    @ApiModelProperty("贷款金额")
    private ValueUnitDTO loanAmount;
    @ApiModelProperty("剩余贷款金额")
    private ValueUnitDTO loanBalanceAmount;
    @ApiModelProperty("本月应还金额")
    private ValueUnitDTO repayTotalAmount;
    @ApiModelProperty("本月应还本金")
    private ValueUnitDTO repayPrincipalAmount;
    @ApiModelProperty("本月应还利息")
    private ValueUnitDTO repayInterestAmount;
    @ApiModelProperty("本月应还日期")
    private String repayDate;
    @ApiModelProperty("本月已还金额")
    private ValueUnitDTO actualRepayAmount;
    @ApiModelProperty("本月未还金额")
    private ValueUnitDTO repayBalanceAmount;
    @ApiModelProperty("本月支付日期")
    private String actualRepayDate;
    @ApiModelProperty(value = "子列表信息")
    private List<SubListInfo> subListInfo;

    @Data
    public static class SubListInfo {
        @ApiModelProperty("关联项目名称")
        private String relatedProjName;
        @ApiModelProperty("关联合同编号")
        private String relatedContractCode;
        @ApiModelProperty("租金回笼金额")
        private ValueUnitDTO rentPlanCollectionAmount;
        @ApiModelProperty("租金回笼日期")
        private LocalDate rentPlanCollectionDate;
        @ApiModelProperty("账户性质code")
        private String bankAccountTypeCode;
        @ApiModelProperty("账户性质display")
        private String bankAccountTypeDisplay;
    }
}
