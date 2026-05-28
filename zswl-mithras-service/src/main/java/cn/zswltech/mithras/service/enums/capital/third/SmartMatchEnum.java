package cn.zswltech.mithras.service.enums.capital.third;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/5/23/17:46
 * @description
 */
@Getter
@AllArgsConstructor
public enum SmartMatchEnum {

    MATCHED("1", "已匹配"),
    UNMATCHED("0", "未匹配");

    private final String code;
    private final String display;
}
