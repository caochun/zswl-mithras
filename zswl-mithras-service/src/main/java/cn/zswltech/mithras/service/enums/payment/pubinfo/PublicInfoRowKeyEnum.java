package cn.zswltech.mithras.service.enums.payment.pubinfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bigbear
 * @date 2024/9/10 21:34
 * @description
 */
@Getter
@AllArgsConstructor
public enum PublicInfoRowKeyEnum {
    QCC_CHANGE_RECORD("企查查-基本信息-变更记录"),
    QCC_EQUITY_PENETRATION("企查查-基本信息-股权穿透图"),
    QCC_OPERATING_RISK("企查查-经营风险"),
    QCC_LEGAL_LITIGATION("企查查-法律诉讼"),
    QCC_ASSOCIATION_RISK("企查查-关联风险"),
    QYYJT_BOND_DEFAULT("企业预警通-债券违约"),
    ZDW_REGISTRATION_DETAILS("中登网-登记详细信息列表"),
    QG_COURT_EXECUTIONER("全国法院被执行人或被纳入失信人查询【适用于担保自然人】"),
    CREDIT_REPORT("征信报告"),
    OTHER_SUPPLEMENTARY("其他补充"),
    ;

    private final String display;
}
