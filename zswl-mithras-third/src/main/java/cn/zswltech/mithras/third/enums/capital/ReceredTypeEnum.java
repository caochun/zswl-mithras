package cn.zswltech.mithras.third.enums.capital;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/5/23/17:44
 * @description
 */
@Getter
@AllArgsConstructor
public enum ReceredTypeEnum {

    POSTED("3", "已入账"),
    UN_POSTED("0", "待入账");

    private String code;
    private String display;
}
