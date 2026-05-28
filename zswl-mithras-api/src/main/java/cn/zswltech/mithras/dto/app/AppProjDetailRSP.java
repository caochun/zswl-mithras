package cn.zswltech.mithras.dto.app;

import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import cn.zswltech.mithras.dto.projestablish.priceaoc.ProjEstablishAocPriceRSP;
import cn.zswltech.mithras.dto.projestablish.pricefactoring.ProjEstablishFactoringPriceRSP;
import cn.zswltech.mithras.dto.projestablish.pricelease.ProjEstablishLeasePriceRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author junke
 */
@ApiModel("融租易APP我的项目列表-返回体")
@Data
public class AppProjDetailRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "项目阶段")
    private String stage;

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
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称")
    private String projName;

    /**
     * 项目编号
     */
    @ApiModelProperty(value = "项目编号")
    private String projCode;

    @ApiModelProperty(value = "承租人列表")
    private List<AppPersonInfo> lesseeInfo;

    /**
     * 担保人信息
     */
    @ApiModelProperty(value = "担保人信息")
    private List<AppPersonInfo> guaranteeInfo;

    @ApiModelProperty(value = "租赁报价方案")
    private LeasePriceDetailRSP leasePriceRSP;

    @ApiModelProperty(value = "债权转让报价方案")
    private AocPriceDetailRSP aocPriceRSP;

    @ApiModelProperty(value = "保理报价方案")
    private FactoringPriceDetailRSP factoringPriceRSP;


    /**
     * 授信主体存量风险敞口
     */
    @ApiModelProperty(value = "授信主体存量风险敞口")
    private Long clientRiskExposure;

    /**
     * 申报授信金额
     */
    @ApiModelProperty(value = "申报授信金额")
    private Long applyCreditAmount;

    /**
     * 额度有效期限月数
     */
    @ApiModelProperty(value = "额度有效期限月数")
    private Integer validMonthCount;

    /**
     * 额度是否可循环
     */
    @ApiModelProperty(value = "额度是否可循环")
    private Integer creditAmountLoop;

    @ApiModelProperty("风控行业分类")
    private String riskControlIndustryClassify;
}
