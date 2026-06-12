package cn.zswltech.mithras.third.tianyancha.application.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author luyi
 */
@Data
@Accessors(chain = true)
public class MithrasBaseInfo {
    private static final Map<String, String> TYC_PROVINCE_DISPLAY_MAP = new HashMap<>();

    static {
        TYC_PROVINCE_DISPLAY_MAP.put("gz", "贵州省");
        TYC_PROVINCE_DISPLAY_MAP.put("heb", "河北省");
        TYC_PROVINCE_DISPLAY_MAP.put("sx", "山西省");
        TYC_PROVINCE_DISPLAY_MAP.put("tj", "天津市");
        TYC_PROVINCE_DISPLAY_MAP.put("nmg", "内蒙古自治区");
        TYC_PROVINCE_DISPLAY_MAP.put("hlj", "黑龙江省");
        TYC_PROVINCE_DISPLAY_MAP.put("bj", "北京市");
        TYC_PROVINCE_DISPLAY_MAP.put("ln", "辽宁省");
        TYC_PROVINCE_DISPLAY_MAP.put("sh", "上海市");
        TYC_PROVINCE_DISPLAY_MAP.put("jl", "吉林省");
        TYC_PROVINCE_DISPLAY_MAP.put("js", "江苏省");
        TYC_PROVINCE_DISPLAY_MAP.put("ah", "安徽省");
        TYC_PROVINCE_DISPLAY_MAP.put("zj", "浙江省");
        TYC_PROVINCE_DISPLAY_MAP.put("jx", "江西省");
        TYC_PROVINCE_DISPLAY_MAP.put("fj", "福建省");
        TYC_PROVINCE_DISPLAY_MAP.put("sd", "山东省");
        TYC_PROVINCE_DISPLAY_MAP.put("hen", "河南省");
        TYC_PROVINCE_DISPLAY_MAP.put("gx", "广西壮族自治区");
        TYC_PROVINCE_DISPLAY_MAP.put("hun", "湖南省");
        TYC_PROVINCE_DISPLAY_MAP.put("gd", "广东省");
        TYC_PROVINCE_DISPLAY_MAP.put("hub", "湖北省");
        TYC_PROVINCE_DISPLAY_MAP.put("yn", "云南省");
        TYC_PROVINCE_DISPLAY_MAP.put("sc", "四川省");
        TYC_PROVINCE_DISPLAY_MAP.put("han", "海南省");
        TYC_PROVINCE_DISPLAY_MAP.put("cq", "重庆市");
        TYC_PROVINCE_DISPLAY_MAP.put("snx", "陕西省");
        TYC_PROVINCE_DISPLAY_MAP.put("xz", "西藏自治区");
        TYC_PROVINCE_DISPLAY_MAP.put("gs", "甘肃省");
        TYC_PROVINCE_DISPLAY_MAP.put("qh", "青海省");
        TYC_PROVINCE_DISPLAY_MAP.put("nx", "宁夏回族自治区");
        TYC_PROVINCE_DISPLAY_MAP.put("xj", "新疆维吾尔自治区");
        TYC_PROVINCE_DISPLAY_MAP.put("tw", "台湾省");
        TYC_PROVINCE_DISPLAY_MAP.put("hk", "香港特别行政区");
        TYC_PROVINCE_DISPLAY_MAP.put("mo", "澳门特别行政区");
    }

    @ApiModelProperty("是否三证合一")
    private Boolean tripleCertInOne;

    @ApiModelProperty("中征码")
    private String zhongZhengCode;

    @ApiModelProperty("组织机构代码")
    private String orgCode;

    /**
     * 营业执照号
     */
    @ApiModelProperty("营业执照号")
    private String bizLicenseCode;

    /**
     * 存续状态
     */
    @ApiModelProperty("存续状态")
    private String continuousStatus;

    /**
     * 成立日期
     */
    @ApiModelProperty("成立日期")
    private LocalDate establishDate;


    @ApiModelProperty("核准日期")
    private LocalDate approvalDate;
    /**
     * 营业许可证是否为长期
     */
    @ApiModelProperty("营业许可证是否为长期")
    private Boolean bizLicenceLongTerm;

    /**
     * 营业许可证到期日(如果许可证是非长期类型)
     */
    @ApiModelProperty("营业许可证到期日；营业许可证为长期的，此值可为空")
    private LocalDate bizLicenseEndDate;

    /**
     * 业务范围
     */
    @ApiModelProperty("业务范围")
    private String bizScope;

    /**
     * 行业分类
     */
    @ApiModelProperty("行业分类")
    private String industryType;

    /**
     * 当前分类所归属的父分类
     */
    @ApiModelProperty("带有父分类的行业分类列表")
    private List<String> industryTypeWithParent;

    /**
     * 经济类型
     */
    @ApiModelProperty("经济类型")
    private String economyType;

    /**
     * 组织机构类型
     */
    @ApiModelProperty("组织机构类型")
    private String orgType;

    /**
     * 企业规模
     */
    @ApiModelProperty("企业规模")
    private String orgScale;

    /**
     * 注册币种
     */
    @ApiModelProperty("注册币种")
    private String registerCurrencyType;

    /**
     * 注册资本
     */
    @ApiModelProperty("注册资本")
    private Long registerCapital;

    /**
     * 实收币种
     */
    @ApiModelProperty("实收币种")
    private String realCurrencyType;

    /**
     * 实收资本
     */
    @ApiModelProperty("实收资本")
    private Long realCapital;

    private Long registerCapitalRate;

    /**
     * 法人代表
     */
    @ApiModelProperty("法人代表")
    private String corpRepresent;

    /**
     * 法人性别
     */
    @ApiModelProperty("法人性别")
    private String corpGender;

    /**
     * 法人证件类型
     */
    @ApiModelProperty("法人证件类型")
    private String corpCertType;

    /**
     * 法人证件号码
     */
    @ApiModelProperty("法人证件号码")
    private String corpCertCode;
    private String clientType;
    private Boolean listedCompany;
    private String base;
    private String district;
    private String city;
    private String regLocation;
    private String tycName;
    /**
     * 客户名称
     */
    private String clientName;

    public String getRegLocation() {
        return removePrefix(removePrefix(removePrefix(regLocation, TYC_PROVINCE_DISPLAY_MAP.get(base)), city), district);
    }

    private static String removePrefix(String value, String prefix) {
        if (value == null || prefix == null || !value.startsWith(prefix)) {
            return value;
        }
        return value.substring(prefix.length());
    }
}
