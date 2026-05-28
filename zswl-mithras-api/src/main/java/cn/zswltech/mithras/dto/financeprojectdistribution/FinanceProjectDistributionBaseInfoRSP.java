package cn.zswltech.mithras.dto.financeprojectdistribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@Data
@ApiModel("项目利润分配-基本信息-返回参数")
public class FinanceProjectDistributionBaseInfoRSP {
    @ApiModelProperty("基本信息id")
    private Long id;
    @ApiModelProperty("项目利润分配id")
    private Long projectProfitDistributionId;
    @ApiModelProperty(value = "客户Name")
    private String clientName;
    @ApiModelProperty(value = "风控行业分类")
    private String riskControlIndustryClassify;
    @ApiModelProperty(value = "合同编号")
    private String contractCode;
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
    @ApiModelProperty(value = "项目类型：公共事业类、省内国（央）企、其他")
    private String projectType;
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
    @ApiModelProperty(value = "项目分类")
    private String projItem;
    @ApiModelProperty(value = "备注")
    private String remark;
}
