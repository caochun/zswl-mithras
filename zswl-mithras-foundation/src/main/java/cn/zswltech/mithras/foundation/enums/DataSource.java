package cn.zswltech.mithras.foundation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2025/9/26
 * @description
 */
@Getter
@AllArgsConstructor
public enum DataSource {
    MANUAL("手工创建"),
    SYSTEM("系统生成");

    private final String display;
}
