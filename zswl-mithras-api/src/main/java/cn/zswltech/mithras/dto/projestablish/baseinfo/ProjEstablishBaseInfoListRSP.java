package cn.zswltech.mithras.dto.projestablish.baseinfo;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author luyi
 * @description 立项基本信息表
 * @date 2022-07-19
 */
@Data
@ApiModel("立项基本信息表列表-返回体")
public class ProjEstablishBaseInfoListRSP extends ListBaseRSP {

    /**
     * 客户id
     */
    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

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
     * 存量翻单、渠道介绍、自主开发
     */
    @ApiModelProperty(value = "存量翻单、渠道介绍、自主开发")
    private String projSource;

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

    @ApiModelProperty("评估主体ID")
    private Long evaluationSubjectId;

    @ApiModelProperty("地区分类 ProjRegionalClassify")
    private String regionalProjectClassify;

    @ApiModelProperty("评估主体名称")
    private String evaluationSubjectName;

    /**
     * 评级字段
     */
    @ApiModelProperty(value = "客户评级id")
    private Long ratingClientId;

    @ApiModelProperty("评估主体评级")
    private String ratingFinalScore;

    @ApiModelProperty(value = "主承租人客户评级id")
    private Long ratingMainClientId;

    @ApiModelProperty("主承租人评估主体评级")
    private String ratingMainFinalScore;

    @ApiModelProperty(value = "债项评级id")
    private Long ratingAmountId;

    @ApiModelProperty("债项评级参考额度")
    private String ratingQuota;

    @ApiModelProperty("评级更新时间")
    private Date ratingUpdateTime;

    /**
     * 风控行业分类
     */
    @ApiModelProperty(value = "风控行业分类 RiskControlIndustryClassify")
    private String riskControlIndustryClassify;


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

    @ApiModelProperty(value = "地区信息")
    private String areaName;

    /**
     * 转让方。（项目类型为租赁时）
     */
    @ApiModelProperty(value = "转让方。（项目类型为租赁时）")
    private String assignor;

    /**
     * 承租人列表
     */
    @ApiModelProperty(value = "承租人列表")
    private List<ProjEstablishPersonInfo> lesseeInfo;

    /**
     * 债权人id
     */
    @ApiModelProperty(value = "债权人列表")
    private List<ProjEstablishPersonInfo> creditorInfo;

    /**
     * 债务人信息。（项目类型为保理时）
     */
    @ApiModelProperty(value = "债务人信息。（项目类型为保理时）")
    private List<ProjEstablishPersonInfo> debtorInfo;

    /**
     * 担保人信息
     */
    @ApiModelProperty(value = "担保人信息")
    private List<ProjEstablishPersonInfo> guaranteeInfo;

    /**
     * 质押人信息
     */
    @ApiModelProperty(value = "质押人信息")
    private List<ProjEstablishPersonInfo> pledgorInfo;

    /**
     * 抵押人信息
     */
    @ApiModelProperty(value = "抵押人信息")
    private List<ProjEstablishPersonInfo> mortgagorInfo;

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

    @ApiModelProperty(value = "风控经理ids")
    private List<Long> riskControlManagerId;

    @ApiModelProperty(value = "风控经理名称")
    private List<String> riskControlManagerName;

    @ApiModelProperty("立项状态")
    private String projEstablishStatus;

    @ApiModelProperty("审批状态")
    private String projEstablishProcessStatus;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("是否为项目主办")
    private Boolean isProjSponsor;

    @ApiModelProperty("备注")
    private String remark;

    /**
     * 供应商列表
     */
    @ApiModelProperty(value = "供应商列表")
    private String supplierInfo;

}
