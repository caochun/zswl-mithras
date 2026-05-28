package cn.zswltech.mithras.service.enums.capital;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/5/21/11:59
 * @description
 */
@Getter
@AllArgsConstructor
public enum CollectionPaymentIdKeyEnum {
    INDIRECT_PAYMENT("间融还款"),
    INDIRECT_COLLECTION("间融收款"),
    DIRECT_PAYMENT("直融还款"),
    DIRECT_COLLECTION("直融收款"),
    ;

    private final String display;

    public static CollectionPaymentIdKeyEnum of(String name) {
        for (CollectionPaymentIdKeyEnum collectionPaymentIdKeyEnum : values()) {
            if (collectionPaymentIdKeyEnum.name().equals(name)) {
                return collectionPaymentIdKeyEnum;
            }
        }
        return null;
    }
}
