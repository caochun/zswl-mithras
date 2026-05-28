package cn.zswltech.mithras.dto.fund.financing.baseinfo;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/2/20
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("融资管理-基本信息详情-返回体")
public class FundFinancingBaseInfoDetailRSP extends ListBaseRSP {
    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("融资编号")
    private String financingCode;

    @ApiModelProperty("融资机构id")
    private List<Long> organizationId;

    @ApiModelProperty("融资机构名称")
    private List<String> organizationName;

    @ApiModelProperty("总授信额度")
    private Long totalCreditLimit;

    @ApiModelProperty("剩余授信额度")
    private Long remainingCreditLimit;

    @ApiModelProperty("期限类型")
    private String timeLimitType;

    @ApiModelProperty("业务类型")
    private String businessType;

    @ApiModelProperty("担保信息")
    private List<GuaranteeInfoRSP> guaranteeInfoList;

    @ApiModelProperty("机构信息")
    private List<OrganizationInfo> organizationInfoList;

    @ApiModelProperty("资金用途")
    private String fundsPurpose;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("资金经理id")
    private Long fundManagerId;

    @ApiModelProperty("资金经理名称")
    private String fundManagerName;

    @ApiModelProperty("所属部门id")
    private Long deptId;

    @ApiModelProperty("所属部门名称")
    private String deptName;

    @ApiModelProperty("部门负责人id")
    private Long bizHeaderId;

    @ApiModelProperty("部门负责人名称")
    private String bizHeaderName;

    @ApiModelProperty("分管领导id")
    private Long leaderId;

    @ApiModelProperty("分管领导名称")
    private String leaderName;

    @ApiModelProperty("融资状态")
    private String financingStatus;

    @ApiModelProperty("审批状态")
    private String approvalStatus;

    @ApiModelProperty("计划贷款时间")
    private String planLoanDate;

    @ApiModelProperty("实际贷款时间")
    private String actualLoanDate;

    @ApiModelProperty("是否期初一次性收息 0-否 1-是")
    private Integer initialInterestReceivedOnce;

    @Data
    public static class OrganizationInfo {

        private Long organizationId;

        private String organizationName;

        private Long organizationAmount;

        private Long remainingCreditAmount;

    }


    @Data
    public static class GuaranteeInfoRSP {
        @ApiModelProperty("融资机构id")
        private Long organizationId;

        @ApiModelProperty("融资机构名称")
        private String organizationName;

        @ApiModelProperty("担保机构id")
        private Long guaranteeAgencyId;

        @ApiModelProperty("担保机构名称")
        private String guaranteeAgencyName;

        @ApiModelProperty("担保金额")
        private Long guaranteeAmount;

        @ApiModelProperty("剩余可担保金额")
        private Long remainingGuaranteeAmount;
    }
}
