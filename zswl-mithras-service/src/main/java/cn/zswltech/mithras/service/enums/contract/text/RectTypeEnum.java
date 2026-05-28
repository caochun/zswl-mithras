package cn.zswltech.mithras.service.enums.contract.text;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bigbear
 * @date 2024/11/28 19:03
 * @description
 */
@Getter
@AllArgsConstructor
public enum RectTypeEnum {

    SEAL_PERSONAL("个人签名"),
    SEAL_CORPORATE("公司公章"),
    TIMESTAMP("时间戳"),
    ACROSS_PAGE_ODD("奇数页骑缝章"),
    ACROSS_PAGE("骑缝章"),
    ;

    private final String display;
}
