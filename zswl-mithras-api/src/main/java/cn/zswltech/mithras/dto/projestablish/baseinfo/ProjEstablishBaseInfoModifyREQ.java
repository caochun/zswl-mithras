package cn.zswltech.mithras.dto.projestablish.baseinfo;

import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author luyi
 * @description 立项基本信息表
 * @date 2022-07-19
 */
@Data
@ApiModel("立项基本信息表编辑-请求体")
public class ProjEstablishBaseInfoModifyREQ {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 租赁类型。直租、回租、经营性租赁
     */
    @ApiModelProperty(value = "租赁类型。直租、回租、经营性租赁")
    private List<String> leaseTypes;

    /**
     *  风控行业分类
     */
    @ApiModelProperty(value = "风控行业分类")
    private String riskControlIndustryClassify;

    /**
     * 存量翻单、渠道介绍、自主开发
     */
    @NotBlank
    @ApiModelProperty(value = "存量翻单、渠道介绍、自主开发")
    private String projSource;

    /**
     * 资金用途
     */
    @ApiModelProperty(value = "资金用途")
    private String fundsPurpose;

    @ApiModelProperty("评估主体ID")
    private Long evaluationSubjectId;

    @ApiModelProperty("地区分类 ProjRegionalClassify")
    private String regionalProjectClassify;

    /**
     * 项目背景
     */
    @ApiModelProperty(value = "项目背景")
    private String projBackground;

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
     * 转让方。（项目类型为转租赁时）
     */
    @ApiModelProperty(value = "转让方。（项目类型为转租赁时）")
    private String assignor;

    /**
     * 承租人列表
     */
    @ApiModelProperty(value = "承租人列表")
    private List<ProjEstablishPersonInfo> lesseeInfo;

    /**
     * 债权人列表
     */
    @ApiModelProperty(value = "债权人列表（项目类型为保理时）")
    private List<ProjEstablishPersonInfo> creditorInfo;

    /**
     * 债务人信息。（项目类型为保理时）
     */
    @Valid
    @ApiModelProperty(value = "债务人信息。（项目类型为保理时）")
    private List<ProjEstablishPersonInfo> debtorInfo;

    /**
     * 担保人信息
     */
    @Valid
    @ApiModelProperty(value = "担保人信息")
    private List<ProjEstablishPersonInfo> guaranteeInfo;

    /**
     * 质押人信息
     */
    @Valid
    @ApiModelProperty(value = "质押人信息")
    private List<ProjEstablishPersonInfo> pledgorInfo;

    /**
     * 抵押人信息
     */
    @Valid
    @ApiModelProperty(value = "抵押人信息")
    private List<ProjEstablishPersonInfo> mortgagorInfo;

    /**
     * 项目主办用户id
     */
    @NotNull
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
    @NotNull
    @ApiModelProperty(value = "业务部门id")
    private Long bizDeptId;

    /**
     * 业务部门负责人id
     */
    @NotNull
    @ApiModelProperty(value = "业务部门负责人id")
    private Long bizDeptLeaderId;

    /**
     * 业务分管领导id
     */
    @NotNull
    @ApiModelProperty(value = "业务分管领导id")
    private Long bizDivisionLeaderId;

    @ApiModelProperty("备注")
    private String remark;

    /**
     * 供应商列表
     */
    @ApiModelProperty(value = "供应商列表")
    private String supplierInfo;

}
