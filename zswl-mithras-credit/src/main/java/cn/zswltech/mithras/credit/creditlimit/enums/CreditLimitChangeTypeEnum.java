package cn.zswltech.mithras.credit.creditlimit.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2024/9/4
 * @description
 */
@AllArgsConstructor
@Getter
public enum CreditLimitChangeTypeEnum {
    // 占用
    OCCUPY,
    // 释放
    RELEASE
}
