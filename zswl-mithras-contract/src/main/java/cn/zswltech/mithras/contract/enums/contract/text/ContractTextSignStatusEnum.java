package cn.zswltech.mithras.contract.enums.contract.text;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bigbear
 * @date 2024/12/9 10:40
 * @description
 */
@Getter
@AllArgsConstructor
public enum ContractTextSignStatusEnum implements PullDown {
    TO_BE_SIGN("待签约"),
    SIGNED("已签约"),
    ;

    private final String display;

    public ContractTextSignStatusEnum findByName(String name) {
        for (ContractTextSignStatusEnum item : ContractTextSignStatusEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }
}
