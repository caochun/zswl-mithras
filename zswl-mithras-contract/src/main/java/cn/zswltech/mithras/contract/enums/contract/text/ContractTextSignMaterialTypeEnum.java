package cn.zswltech.mithras.contract.enums.contract.text;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bigbear
 * @date 2024/11/29 17:21
 * @description
 */
@Getter
@AllArgsConstructor
public enum ContractTextSignMaterialTypeEnum {
    CONVERTED("已转换"),
    SIGNED("已签约"),
    ;

    private final String display;
}
