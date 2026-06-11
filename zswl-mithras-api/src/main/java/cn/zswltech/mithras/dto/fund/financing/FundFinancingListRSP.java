package cn.zswltech.mithras.dto.fund.financing;

import cn.zswltech.mithras.api.common.PageR;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/2/20
 * @description
 */
@Data
@ApiModel("融资管理-首页列表-返回体")
public class FundFinancingListRSP{
    @ApiModelProperty("列表记录")
    private PageR<FundFinancingListRSP.FundFinancingList> records;

    @ApiModelProperty("合计")
    private FundFinancingListRSP.Sum sum;
    @Data
    public static class FundFinancingList {
        @ApiModelProperty("融资id")
        private Long id;

        @ApiModelProperty("融资编号")
        private String financingCode;

        @ApiModelProperty("融资机构id")
        private List<Long> organizationId;

        @ApiModelProperty("融资机构名称")
        private List<String> organizationName;

        @ApiModelProperty("融资金额")
        private Long financingAmount;

        @ApiModelProperty("剩余本金")
        private Long lastPrincipal;

        @ApiModelProperty("担保融资金额")
        private Long guaranteeFinancingAmount;

        @ApiModelProperty("信用融资金额")
        private Long creditFinancingAmount;

        @ApiModelProperty("实际综合成本")
        private Integer actualComprehensiveCost;

        @ApiModelProperty("借款年利率")
        private Integer interestRate;

        @ApiModelProperty("利率类型")
        private String interestRateType;

        @ApiModelProperty("借款日期")
        private String borrowDate;

        @ApiModelProperty("到期日期")
        private String expireDate;

        @ApiModelProperty("融资状态")
        private String financingStatus;

        @ApiModelProperty("审批状态")
        private String approvalStatus;

        @ApiModelProperty("贷后变更子类型")
        private String changeSubType;

        @ApiModelProperty("合同编号列表")
        private List<String> contractCodeList;

        @ApiModelProperty("创建人id")
        private Long createUserId;

        @ApiModelProperty("创建人名称")
        private String createUserName;

        @ApiModelProperty("创建时间")
        private String createTime;

        @ApiModelProperty("修改时间")
        private String updateTime;

        @ApiModelProperty("综合融资成本")
        private Integer comprehensiveFinancingCost;

        @ApiModelProperty("业务类型")
        private String businessType;

    }

    @Data
    public static class Sum{
        @ApiModelProperty("融资金额合计")
        private Long financingAmount;

        @ApiModelProperty("剩余本金合计")
        private Long lastPrincipal;

        @ApiModelProperty("担保融资金额合计")
        private Long guaranteeFinancingAmount;

        @ApiModelProperty("信用融资金额合计")
        private Long creditFinancingAmount;

        @ApiModelProperty("实际综合成本合计")
        private Integer actualComprehensiveCost;

        @ApiModelProperty("综合融资成本")
        private Integer comprehensiveFinancingCost;

        @ApiModelProperty("借款年利率")
        private Integer interestRate;
    }

}

