package cn.zswltech.mithras.dto.contract.baseinfo;

import cn.zswltech.mithras.dto.ListBaseRSP;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * @author vico
 * @description 合同基本信息表
 * @date 2022-08-12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("合同基本信息详情表列表-请求体")
public class ContractBaseInfoDetailRSP extends ListBaseRSP {

    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "客户id")
    private Long clientId;
    @ApiModelProperty(value = "客户Name")
    private String clientName;
    @ApiModelProperty(value = "风控行业分类")
    private String riskControlIndustryClassify;
    @ApiModelProperty(value = "合同编号")
    private String contractCode;
    @ApiModelProperty(value = "咨询合同编号")
    private String consultingContractCode;
    @ApiModelProperty(value = "剩余可用额度(元)")
    private Long remainAvailableQuota;
    @ApiModelProperty(value = "项目名称")
    private String projName;
    @ApiModelProperty(value = "项目编号")
    private String projCode;
    @ApiModelProperty(value = "项目批复金额")
    private Long approvedAmount;
    @ApiModelProperty(value = "业务类型。租赁、保理、转租赁")
    private String bizType;
    @ApiModelProperty(value = "租赁类型。直租、回租、经营性租赁")
    private String leaseType;
    @ApiModelProperty(value = "保理类型")
    private String factoringType;
    @ApiModelProperty(value = "转让类型")
    private String zrType;
    @ApiModelProperty(value = "转让方")
    private String assignor;
    @ApiModelProperty(value = "项目类型：公共事业类、省内国（央）企、其他")
    private String projectType;
    @ApiModelProperty(value = "风险等级")
    private String riskLevel;
    @ApiModelProperty(value = "项目来源：存量翻单、渠道介绍、自主开发")
    private String projSource;
    @ApiModelProperty(value = "资金用途")
    private String fundsPurpose;
    @ApiModelProperty(value = "项目背景")
    private String projBackground;
    @ApiModelProperty(value = "项目主办用户id")
    private Long projSponsorUserId;
    @ApiModelProperty(value = "项目主办用户名称")
    private String projSponsorUserName;
    @ApiModelProperty(value = "项目协办方用户id列表")
    private List<Long> projCosponsorUserIds;
    @ApiModelProperty(value = "项目协办方用户名称列表")
    private List<String> projCosponsorUserNames;
    @ApiModelProperty(value = "业务部门id")
    private Long bizDeptId;
    @ApiModelProperty(value = "业务部门名称")
    private String bizDeptName;
    @ApiModelProperty(value = "业务部门负责人id")
    private Long bizDeptLeaderId;
    @ApiModelProperty(value = "业务部门负责人名称")
    private String bizDeptLeaderName;
    @ApiModelProperty(value = "业务分管领导id")
    private Long bizDivisionLeaderId;
    @ApiModelProperty(value = "业务分管领导名称")
    private String bizDivisionLeaderName;
    @ApiModelProperty(value = "关联的评审id")
    private Long projReviewId;
    @ApiModelProperty(value = "项目分类")
    private String projItem;

    @ApiModelProperty(value = "计划付款日期")
    @JsonIgnore
    private LocalDate paymentPlanDate;
    /*@ApiModelProperty(value ="计划付款金额-合同金额")
    @JsonIgnore
    private Long paymentPlanAmount;*/
    @JsonIgnore
    @ApiModelProperty(value = "支付申请次数")
    private Long paymentCount;
    @ApiModelProperty(value = "风控经理id")
    @JsonIgnore
    private Long riskControlManagerId;
    /**
     * 概算起租日
     **/
    @ApiModelProperty(value = "estimated_lease_date")
    private LocalDate estimatedLeaseDate;

    @ApiModelProperty(value = "实际起租日")
    private LocalDate actualLeaseDate;

    @ApiModelProperty(value = "版本表id")
    private Long editionId;

    @ApiModelProperty(value = "是否主办")
    private Boolean isProjSponsor;

    @ApiModelProperty("合同状态")
    private String contractStatus;

    @ApiModelProperty("合同流程状态")
    private String contractProcessStatus;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "变更说明")
    private String changeRemark;

    @ApiModelProperty(value = "存量合同标识，0否 1是")
    private Integer stockContractFlag;

    @ApiModelProperty(value = "调整说明")
    private String adjustRemark;

    @ApiModelProperty(value = "结清说明")
    private String settleRemark;

    @ApiModelProperty(value = "当前处理人")
    private String curAssigneeIds;

    @ApiModelProperty(value = "确认收入方式 IncomeConfirmTypeEnum")
    private String incomeConfirmType;

    @ApiModelProperty(value = "合同金额")
    private Long contractAmount;

    @ApiModelProperty(value = "租赁物类型 LeaseItemTypeEnum")
    private List<String> leaseItemTypes;

}
