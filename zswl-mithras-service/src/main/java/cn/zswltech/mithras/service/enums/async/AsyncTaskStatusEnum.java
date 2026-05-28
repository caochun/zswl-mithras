package cn.zswltech.mithras.service.enums.async;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/8/21/17:02
 * @description
 */
@Getter
@AllArgsConstructor
public enum AsyncTaskStatusEnum {
    RUNNING("运行中"),
    SUCCESS("成功"),
    FAIL("失败"),
    ;

    private final String display;

    public static AsyncTaskStatusEnum of(String name) {
        for (AsyncTaskStatusEnum value : AsyncTaskStatusEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
