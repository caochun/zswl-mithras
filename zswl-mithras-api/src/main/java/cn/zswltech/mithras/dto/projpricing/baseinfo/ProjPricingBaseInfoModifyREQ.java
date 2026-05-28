package cn.zswltech.mithras.dto.projpricing.baseinfo;

import cn.zswltech.mithras.dto.client.client.ClientInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 立项基本信息表
 * @date 2022-08-02
 */
@Data
@ApiModel("项目评审基本信息表编辑-请求体")
public class ProjPricingBaseInfoModifyREQ {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为null")
    private Long id;

    /**
     * 客户id
     */
    @ApiModelProperty(value = "客户id")
    private Long clientId;

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
     * @deprecated 客户的风控行业分类替换该字段功能
     */
    @Deprecated
    @ApiModelProperty(value = "下拉框选项：公共事业类、省内国（央）企、其他。n内容决定后续审批流审批权限。")
//    @NotNull(message = "项目类型为null")
    private String projectType;

    @ApiModelProperty(value = "项目分类")
    //@NotNull(message = "项目分类为空")
    private String projectClassify;
//
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
     * 保理类型
     */
    @ApiModelProperty(value = "保理类型")
    private List<String> factoringTypes;

    /**
     * 保理类型
     */
    @ApiModelProperty(value = "转让类型")
    private List<String> zrTypes;

    @ApiModelProperty(value = "区域划分 ProjRegionalDivisionEnum")
    private String regionalDivision;

    /**
     * 存量翻单、渠道介绍、自主开发
     */
    @ApiModelProperty(value = "存量翻单、渠道介绍、自主开发")
    @NotNull(message = "项目来源为null")
    private String projSource;

    @ApiModelProperty(value = "评估主体")
    private Long evaluationSubjectId;

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
    @NotNull(message = "资金用途为不能为空")
    private String fundsPurpose;

    /**
     * 项目背景
     */
    @ApiModelProperty(value = "项目背景")
    @NotNull(message = "项目背景为null")
    private String projBackground;

    /**
     * 转让方。（项目类型为转租赁时）
     */
    @ApiModelProperty(value = "转让方。（项目类型为转租赁时）")
    private String assignor;

    /**
     * 承租人列表
     */
    @ApiModelProperty(value = "承租人列表")
    private List<ClientInfo> lesseeInfo;

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

    /**
     * 项目协办方用户id列表
     */
    @ApiModelProperty(value = "项目协办方用户id列表")
    private List<Long> projCosponsorUserIds;

    /**
     * 业务部门id
     */
    @ApiModelProperty(value = "业务部门id")
    private Long bizDeptId;

    /**
     * 业务部门负责人id
     */
    @ApiModelProperty(value = "业务部门负责人id")
    private Long bizDeptLeaderId;

    /**
     * 业务分管领导id
     */
    @ApiModelProperty(value = "业务分管领导id")
    private Long bizDivisionLeaderId;

    /**
     * 风控经理id
     */
    @ApiModelProperty(value = "风控经理id")
    @NotNull(message = "风控经理为null")
    private Long riskControlManagerId;

    /**
     * legal_manager_user_id
     */
    @ApiModelProperty(value = "legal_manager_user_id")
    @NotNull(message = "法务经理为null")
    private Long legalManagerUserId;

    @ApiModelProperty("定价状态")
    private String projPricingStatus;

    @ApiModelProperty("定价审批状态")
    private String projPricingProcessStatus;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("区域项目分类")
//    @NotNull(message = "区域项目分类不能为空")
    private String regionalProjectClassify;

    @ApiModelProperty("FTP行业分类")
    @NotBlank(message = "FTP行业分类不能为空")
    private String ftpIndustryCategory;

    @ApiModelProperty("项目管理层级")
//    @NotBlank(message = "项目管理层级不能为空")
    private String projectManageLevel;

    @ApiModelProperty("是否AAA评级")
//    @NotNull(message = "是否AAA评级不能为空")
    private Integer isAAA;
    /**
     * 供应商列表
     */
    @ApiModelProperty(value = "供应商列表")
    private String supplierInfo;

}
