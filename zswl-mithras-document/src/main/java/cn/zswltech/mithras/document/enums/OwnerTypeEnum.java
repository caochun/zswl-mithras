package cn.zswltech.mithras.document.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/3/18/19:00
 * @description 文件模版权限拥有者类型
 */
@Getter
@AllArgsConstructor
public enum OwnerTypeEnum {
    /**
     * 权限
     */
    POST(1, "岗位"),
    PERSON(2, "人");

    private final Integer code;
    private final String display;

    public static OwnerTypeEnum ofCode(Integer code) {
        for (OwnerTypeEnum anEnum : OwnerTypeEnum.values()) {
            if (anEnum.code.equals(code)) {
                return anEnum;
            }
        }
        return null;
    }

}
