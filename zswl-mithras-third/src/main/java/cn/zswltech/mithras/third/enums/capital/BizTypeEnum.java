package cn.zswltech.mithras.third.enums.capital;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/5/23/17:27
 * @description
 */
@Getter
@AllArgsConstructor
public enum BizTypeEnum {
    PUBLIC("1", "普通"),
    UP("2", "上划"),
    DOWN("3", "下拨"),
    ;

    private String code;
    private String display;
}
