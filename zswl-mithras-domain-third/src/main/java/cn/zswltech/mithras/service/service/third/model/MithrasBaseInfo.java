package cn.zswltech.mithras.service.service.third.model;

import cn.zswltech.flow.core.util.ApplicationContextUtil;
import cn.zswltech.mithras.service.mapper.corp.GeneralDictionaryMapper;
import cn.zswltech.mithras.service.mapper.model.GeneralDictionary;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

/**
 * @author luyi
 */
@Data
@Accessors(chain = true)
public class MithrasBaseInfo {
    private static final String ENUM_TYC_PROVINCE = "tycProvince";

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
        GeneralDictionaryMapper generalDictionaryMapper = ApplicationContextUtil.getBean(GeneralDictionaryMapper.class);
        GeneralDictionary tycProvince = generalDictionaryMapper.selectOne(Wrappers.<GeneralDictionary>lambdaQuery()
                .eq(GeneralDictionary::getDictKey, ENUM_TYC_PROVINCE)
                .eq(GeneralDictionary::getCode, base)
        );
        if (tycProvince != null) {
            if (regLocation.startsWith(tycProvince.getDisplay())) {
                regLocation = regLocation.substring(tycProvince.getDisplay().length());
            }
            if (regLocation.startsWith(city)) {
                regLocation = regLocation.substring(city.length());
            }
            if (regLocation.startsWith(district)) {
                regLocation = regLocation.substring(district.length());
            }
        }
        return regLocation;
    }
}
