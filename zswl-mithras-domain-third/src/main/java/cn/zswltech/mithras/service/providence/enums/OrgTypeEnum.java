package cn.zswltech.mithras.service.providence.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Jim
 * @version 1.0.0
 * @descripition:
 * @date 2024/12/24 14:28
 */
@AllArgsConstructor
@Getter
public enum OrgTypeEnum {
    // 企业
    ENTERPRISE("enterprise", "企业"),
    // 金融机构
    FINANCIAL_INSTITUTION("financialInstitution", "金融机构"),
    ;
    private String code;
    private String desc;

}
