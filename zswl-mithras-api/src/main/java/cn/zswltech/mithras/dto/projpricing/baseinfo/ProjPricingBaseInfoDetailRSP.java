package cn.zswltech.mithras.dto.projpricing.baseinfo;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhaozhengkang
 * @description 立项基本信息表
 * @date 2022-08-02
 */
@Data
@ApiModel("立项基本信息表列表-返回体")
public class ProjPricingBaseInfoDetailRSP extends ListBaseRSP {

    /**
     * 客户id
     */
    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    @ApiModelProperty(value = "风控行业分类")
    private String riskControlIndustryClassify;

    @ApiModelProperty(value = "地区信息")
    private String areaName;

    /**
     * 业务类型。租赁、保理、转租赁
     */
    @ApiModelProperty(value = "业务类型。租赁、保理、转租赁")
    private String bizType;

    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称")
    private String projName;

    /**
     * 下拉框选项：公共事业类、省内国（央）企、其他。n内容决定后续审批流审批权限。
     */
    @ApiModelProperty(value = "下拉框选项：公共事业类、省内国（央）企、其他。n内容决定后续审批流审批权限。")
    private String projectType;

    @ApiModelProperty(value = "项目分类")
    private String projectClassify;

    @ApiModelProperty(value = "地区项目分类")
    private String regionalProjectClassify;

    @ApiModelProperty(value = "区域划分 ProjRegionalDivisionEnum")
    private String regionalDivision;

//    /**
//     * 审批类型
//     */
//    @ApiModelProperty(value = "审批类型")
//    private String approvalType;

    /**
     * 项目编号
     */
    @ApiModelProperty(value = "项目编号")
    private String projCode;

    /**
     * 租赁类型。直租、回租、经营性租赁
     */
    @ApiModelProperty(value = "租赁类型。直租、回租、经营性租赁")
    private List<String> leaseTypes;

    /**
     * 保理类型。有追明保理、无追明保理、有追暗保理
     */
    @ApiModelProperty(value = "保理类型。有追明保理、无追明保理、有追暗保理")
    private List<String> factoringTypes;

    /**
     * 保理类型
     */
    @ApiModelProperty(value = "转让类型。有追、无追")
    private List<String> zrTypes;

    /**
     * 存量翻单、渠道介绍、自主开发
     */
    @ApiModelProperty(value = "存量翻单、渠道介绍、自主开发")
    private String projSource;

    @ApiModelProperty("评估主体ID")
    private Long evaluationSubjectId;

    @ApiModelProperty("评估主体名称")
    private String evaluationSubjectName;

    /**
     * 国家
     */
    @ApiModelProperty(value = "国家")
    private String country;

    /**
     * 省份
     */
    @ApiModelProperty(value = "省份")
    private String province;

    /**
     * 城市
     */
    @ApiModelProperty(value = "城市")
    private String city;

    /**
     * 区、县
     */
    @ApiModelProperty(value = "区、县")
    private String district;

    /**
     * 资金用途
     */
    @ApiModelProperty(value = "资金用途")
    private String fundsPurpose;

    /**
     * 项目背景
     */
    @ApiModelProperty(value = "项目背景")
    private String projBackground;

    /**
     * 转让方。（项目类型为租赁时）
     */
    @ApiModelProperty(value = "转让方。（项目类型为租赁时）")
    private String assignor;

    /**
     * 承租人列表
     */
    @ApiModelProperty(value = "承租人列表")
    private List<ClientInfo> lesseeInfo;

//    /**
//     * 债权人id
//     */
//    @ApiModelProperty(value = "债权人id")
//    private Long creditorClientId;
//
//    @ApiModelProperty(value = "债权人名称")
//    private String creditorClientName;
//
//    /**
//     * 债权人存量风险敞口
//     */
//    @ApiModelProperty("债权人存量风险敞口")
//    private Long creditorStockRiskExposure;

    /**
     * 债权人列表（项目类型为债权转让时）
     */
    @ApiModelProperty(value = "债权人列表")
    private List<ClientInfo> creditorInfo;

    /**
     * 债务人信息。（项目类型为保理时）
     */
    @ApiModelProperty(value = "债务人信息。（项目类型为保理时）")
    private List<ClientInfo> debtorInfo;
    /**
     * 担保人信息
     */
    @ApiModelProperty(value = "担保人信息")
    private List<ClientInfo> guaranteeInfo;

    /**
     * 质押人信息
     */
    @ApiModelProperty(value = "质押人信息")
    private List<ClientInfo> pledgorInfo;

    /**
     * 抵押人信息
     */
    @ApiModelProperty(value = "抵押人信息")
    private List<ClientInfo> mortgagorInfo;

    /**
     * 项目主办用户id
     */
    @ApiModelProperty(value = "项目主办用户id")
    private Long projSponsorUserId;

    @ApiModelProperty(value = "项目主办用户名称")
    private String projSponsorUserName;

    /**
     * 项目协办方用户id列表
     */
    @ApiModelProperty(value = "项目协办方用户id列表")
    private List<Long> projCosponsorUserIds;

    @ApiModelProperty(value = "项目协办方用户名称列表")
    private List<String> projCosponsorUserNames;

    /**
     * 业务部门id
     */
    @ApiModelProperty(value = "业务部门id")
    private Long bizDeptId;

    @ApiModelProperty(value = "业务部门名称")
    private String bizDeptName;

    /**
     * 业务部门负责人id
     */
    @ApiModelProperty(value = "业务部门负责人id")
    private Long bizDeptLeaderId;

    @ApiModelProperty(value = "业务部门负责人名称")
    private String bizDeptLeaderName;

    /**
     * 业务分管领导id
     */
    @ApiModelProperty(value = "业务分管领导id")
    private Long bizDivisionLeaderId;

    @ApiModelProperty(value = "业务分管领导名称")
    private String bizDivisionLeaderName;

    /**
     * 风控经理id
     */
    @ApiModelProperty(value = "风控经理id")
    private Long riskControlManagerId;

    @ApiModelProperty(value = "风控经理名称")
    private String riskControlManagerName;

    /**
     * legal_manager_user_id
     */
    @ApiModelProperty(value = "legal_manager_user_id")
    private Long legalManagerUserId;

    @ApiModelProperty(value = "法务经理名称")
    private String legalManagerName;

    /**
     * 立项状态
     */
    @ApiModelProperty(value = "定价状态")
    private String projPricingStatus;

    /**
     * 流程状态
     */
    @ApiModelProperty(value = "流程状态")
    private String projPricingProcessStatus;

    @ApiModelProperty(value = "审批流类型")
    private String processModel;

    @ApiModelProperty("是否为项目主办")
    private Boolean isProjSponsor;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("企业性质")
    private String enterpriseNature;

    @ApiModelProperty(value = "当前处理人")
    private String curAssigneeIds;

    @ApiModelProperty("FTP行业分类")
    private String ftpIndustryCategory;

    @ApiModelProperty("项目管理层级")
    private String projectManageLevel;

    @ApiModelProperty("是否AAA评级")
    private Integer isAAA;

    /**
     * 供应商列表
     */
    @ApiModelProperty(value = "供应商列表")
    private String supplierInfo;

}
