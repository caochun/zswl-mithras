package cn.zswltech.mithras.dto.client.commerceinfo;

import cn.zswltech.mithras.dto.ListBaseRSP;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * @author luyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("法人工商信息-返回体")
public class NewCorpCommerceInfoDetailRSP extends ListBaseRSP {

    @ApiModelProperty("id")
    private Long id;


    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty(value = "客户分类")
    private String clientType;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    @ApiModelProperty(value = "客户编号")
    private String clientCode;

    @ApiModelProperty(value = "境内or境外")
    private String domesticOrAbroad;

    @ApiModelProperty(value = "统一社会信用代码")
    private String uscCode;

    @ApiModelProperty(value = "特殊机构代码")
    private String specialOrgCode;

    @ApiModelProperty(value = "是否三证合一")
    private Boolean tripleCertInOne;

    @ApiModelProperty("中征码")
    private String zhongZhengCode;

    @ApiModelProperty(value = "组织机构代码")
    private String orgCode;

    @ApiModelProperty(value = "营业执照号")
    private String bizLicenseCode;
    @ApiModelProperty(value = "存续状态")
    private String continuousStatus;

    @ApiModelProperty(value = "成立日期")
    private LocalDate establishDate;

    @ApiModelProperty(value = "核准日期")
    private LocalDate approvalDate;

    @ApiModelProperty(value = "营业许可证是否为长期")
    private Boolean bizLicenceLongTerm;

    @ApiModelProperty(value = "营业许可证到期日")
    private LocalDate bizLicenseEndDate;

    @ApiModelProperty(value = "业务范围")
    private String bizScope;

    @ApiModelProperty(value = "行业分类")
    private String industryType;

    @ApiModelProperty(value = "风控行业分类")
    private String riskControlIndustryClassify;

    @ApiModelProperty(value = "带有父节点的行业分类code列表")
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
    private String realCapital;

    @ApiModelProperty(value = "注册资本到位率")
    private String registerCapitalRate;

    @ApiModelProperty(value = "法人代表")
    private String corpRepresent;

    @ApiModelProperty(value = "法人性别")
    private String corpGender;

    @ApiModelProperty(value = "法人证件类型")
    private String corpCertType;

    @ApiModelProperty(value = "法人证件号码")
    private String corpCertCode;

//    @ApiModelProperty(value = "是否上市公司")
//    private Boolean listedCompany;

    @ApiModelProperty(value = "企业性质")
    private String enterpriseNature;

    @ApiModelProperty("存在变更历史")
    private Boolean existsHistory = false;

    @ApiModelProperty("是否关联企业")
    private Integer isRelated;

    @ApiModelProperty("是否在沪深主板、中小板、创业板上市")
    private Integer isMarket;

    @ApiModelProperty(value = "是否集团公司")
    private Integer groupFlag;

    @ApiModelProperty(value = "所属集团id")
    private Long belongGroupClientId;

    @ApiModelProperty(value = "所属集团名称")
    private String belongGroupClientName;

    @ApiModelProperty(value = "指标隶属省份")
    private String provinceOfAffiliation;

    @ApiModelProperty(value = "客户权限类型 true有权限， false无权限")
    private boolean clientAuthType;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "权限级别, 管护权为3，申办权为2，查看权为1")
    private Integer level;

    @ApiModelProperty(value = "客户状态,新建或生效")
    private String clientStatus;

}
