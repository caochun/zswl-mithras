package cn.zswltech.mithras.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2024/9/4
 * @description
 */
@AllArgsConstructor
@Getter
public enum CreditLimitStatusEnum {
    WAIT_EFFECTIVE("待生效"),
    EFFECTIVE("已生效"),
    INVALID("已失效");

    private final String display;
}
