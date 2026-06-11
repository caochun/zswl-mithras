package cn.zswltech.mithras.afterlease.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/8/28
 * @description
 */
@Getter
@AllArgsConstructor
public enum AuthCheckSourceSceneEnum implements PullDown {
    AFTER_LEASE_CHECK_REPORT("租后检查报告");

    private final String display;

    @Override
    public String display() {
        return display;
    }
}
