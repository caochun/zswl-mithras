package cn.zswltech.mithras.dto.client.commerceinfo;

import cn.zswltech.mithras.validation.BanSpecialChar;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * @author luyi
 */
@Data
@ApiModel("法人工商信息新增-请求体")
public class CorpCommerceInfoAddREQ {

    @ApiModelProperty("id")
    private Long id;

    @NotNull
    @ApiModelProperty(value = "客户id", required = true)
    private Long clientId;

    @NotBlank
    @ApiModelProperty(value = "客户名称", required = true)
    private String clientName;

    @ApiModelProperty(value = "客户代码", required = true)
    private String clientCode;//todo 客户编号

    @ApiModelProperty("境内or境外")
    private String domesticOrAbroad;

    @ApiModelProperty("特殊机构代码")
    private String specialOrgCode;

    /*@ApiModelProperty(value = "是否三证合一", required = true)
    private Boolean tripleCertInOne;*/

    @ApiModelProperty("中征码")
    @BanSpecialChar
    private String zhongZhengCode;

   /* @ApiModelProperty(value = "组织机构代码", required = true)
    private String orgCode;*/


 /*   @ApiModelProperty(value = "营业执照号", required = true)
    private String bizLicenseCode;*/

    @ApiModelProperty(value = "存续状态")
    private String continuousStatus;
    @ApiModelProperty(value = "成立日期")
    private LocalDate establishDate;
    @ApiModelProperty(value = "核准日期")
    private LocalDate approvalDate;

    /*@NotNull
    @ApiModelProperty(value = "营业许可证是否为长期", required = true)
    private Boolean bizLicenceLongTerm;*/

    @ApiModelProperty(value = "营业许可证到期日")
    private LocalDate bizLicenseEndDate;

    @ApiModelProperty(value = "业务范围")
    @BanSpecialChar
    private String bizScope;

    @ApiModelProperty(value = "行业分类")
    private String industryType;
    @ApiModelProperty(value = "带父分类code的行业分类列表")
    private List<String> industryTypeWithParent;

    @ApiModelProperty(value = "行业分类名称")
    private String industryTypeName;

    @ApiModelProperty(value = "经济类型")
    private String economyType;

    @ApiModelProperty(value = "组织机构类型")
    private String orgType;

    @ApiModelProperty(value = "企业规模")
    private String orgScale;

    @ApiModelProperty(value = "注册币种")
    private String registerCurrencyType;

    @ApiModelProperty(value = "注册资本")
    private Long registerCapital;

    /**
     * 实收币种
     */
    @ApiModelProperty("实收币种")
    private String realCurrencyType;

    @ApiModelProperty(value = "实收资本")
    private Long realCapital;

  /*  @ApiModelProperty(value = "注册资本到位率", required = true)
    private Long registerCapitalRate;*/

    @ApiModelProperty(value = "法人代表", required = true)
    @BanSpecialChar
    private String corpRepresent;

    @ApiModelProperty(value = "法人性别", required = true)
    private String corpGender;

    @ApiModelProperty(value = "法人证件类型", required = true)
    private String corpCertType;

    @ApiModelProperty(value = "法人证件号码", required = true)
    @BanSpecialChar
    private String corpCertCode;

//    @ApiModelProperty(value = "是否上市公司", required = true)
//    private Boolean listedCompany;

    @ApiModelProperty(value = "企业性质", required = true)
    private String enterpriseNature;

    /**
     * 控股类型 {@link cn.zswltech.mithras.customer.enums.client.OwnershipTypeEnum}
     */
    @ApiModelProperty(value = "控股类型")
    private String ownershipType;

    @ApiModelProperty(value = "是否关联企业")
    private Integer isRelated;

    @Deprecated
    @ApiModelProperty(value = "是否在沪深主板、中小板、创业板上市")
    private Integer isMarket;

    //    @NotNull
    @ApiModelProperty(value = "是否集团公司", required = true)
    private Integer groupFlag;

    //    @NotNull
    @ApiModelProperty(value = "所属集团id", required = true)
    private Long belongGroupClientId;

    @ApiModelProperty(value = "风控行业分类", required = true)
    private String riskControlIndustryClassify;


}
