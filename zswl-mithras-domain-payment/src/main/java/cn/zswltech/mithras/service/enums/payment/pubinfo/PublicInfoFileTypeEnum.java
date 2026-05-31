package cn.zswltech.mithras.service.enums.payment.pubinfo;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bigbear
 * @date 2024/9/10 22:38
 * @description
 */
@Getter
@AllArgsConstructor
public enum PublicInfoFileTypeEnum implements PullDown, IMaterialsTypeConvert {
    // 运营上传文件类型
    QCC_BASE_INFO_YY("企查查-基本信息"),
    QCC_CHANGE_RECORD_YY("企查查-基本信息-变更记录"),
    QCC_EQUITY_PENETRATION_YY("企查查-基本信息-股权穿透图"),
    QCC_OPERATING_RISK_YY("企查查-经营风险"),
    QCC_LEGAL_LITIGATION_YY("企查查-法律诉讼"),
    QCC_ASSOCIATION_RISK_YY("企查查-关联风险"),
    QYYJT_BOND_DEFAULT_YY("企业预警通-债券违约"),
    ZDW_REGISTRATION_DETAILS_YY("中登网-登记详细信息列表"),
    CREDIT_REPORT_YY("征信报告"),
    QG_COURT_EXECUTIONER_YY("全国法院被执行人或被纳入失信人查询【适用于担保自然人】"),
    OTHER_SUPPLEMENTARY_YY("其他补充"),

    // 项目经理上传文件类型
    QCC_BASE_INFO_XMJL("企查查-基本信息"),
    QCC_CHANGE_RECORD_XMJL("企查查-基本信息-变更记录"),
    QCC_EQUITY_PENETRATION_XMJL("企查查-基本信息-股权穿透图"),
    QCC_OPERATING_RISK_XMJL("企查查-经营风险"),
    QCC_LEGAL_LITIGATION_XMJL("企查查-法律诉讼"),
    QCC_ASSOCIATION_RISK_XMJL("企查查-关联风险"),
    QYYJT_BOND_DEFAULT_XMJL("企业预警通-债券违约"),
    ZDW_REGISTRATION_DETAILS_XMJL("中登网-登记详细信息列表"),
    CREDIT_REPORT_XMJL("征信报告"),
    QG_COURT_EXECUTIONER_XMJL("全国法院被执行人或被纳入失信人查询【适用于担保自然人】"),
    OTHER_SUPPLEMENTARY_XMJL("其他补充"),
    ;


    public static final String OPERATOR_SUFFIX = "_YY";
    public static final String PROJ_MANAGER_SUFFIX = "_XMJL";

    private final String display;

    @Override
    public String businessModule() {
        return "PUBLIC_INFO";
    }

    @Override
    public String display() {
        return display;
    }

    public static PublicInfoFileTypeEnum find(String name) {
        for (PublicInfoFileTypeEnum value : PublicInfoFileTypeEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
