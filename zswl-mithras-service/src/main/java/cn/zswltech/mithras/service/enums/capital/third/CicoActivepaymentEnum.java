package cn.zswltech.mithras.service.enums.capital.third;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/5/23/17:47
 * @description
 */
@Getter
@AllArgsConstructor
public enum CicoActivepaymentEnum {
    ACTIVE("0", "主动付款"),
    PASSIVE("1", "被动付款");

    private final String code;
    private final String display;

}
