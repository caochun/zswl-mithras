package cn.zswltech.mithras.report.enums.biz;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/1/12/13:46
 * @description 担保人类型
 */
@Getter
@AllArgsConstructor
public enum GuaranteeTypeEnum {
    /**
     * 担保人类型
     */
    GUARANTOR("guarantor"),
    TENANTRY("tenantry");

     private final String value;
}
