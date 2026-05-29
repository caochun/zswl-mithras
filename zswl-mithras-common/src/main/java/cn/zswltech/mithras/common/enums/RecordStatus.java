package cn.zswltech.mithras.common.enums;

import cn.zswltech.mithras.common.enums.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 18:53
 */
public enum RecordStatus implements PullDown {
    NEW("新建"), CLOSED("关闭"), TAKE_EFFECT("生效"), EXPIRE("已失效");

    RecordStatus(String display) {
        this.display = display;
    }

    public final String display;

    public static RecordStatus of(String code) {
        for (RecordStatus value : RecordStatus.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }
}
