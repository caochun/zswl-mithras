package cn.zswltech.mithras.service.enums.async;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/8/21/17:03
 * @description
 */
@Getter
@AllArgsConstructor
public enum AsyncTaskTypeEnum {
    COMMON("普通任务"),
    JOB("定时任务"),
    ;
    private final String display;

    public static AsyncTaskTypeEnum of(String name) {
        for (AsyncTaskTypeEnum value : AsyncTaskTypeEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
