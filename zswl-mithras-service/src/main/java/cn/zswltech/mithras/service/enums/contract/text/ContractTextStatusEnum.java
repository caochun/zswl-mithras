package cn.zswltech.mithras.service.enums.contract.text;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bigbear
 * @date 2024/11/14 18:40
 * @description
 */
@Getter
@AllArgsConstructor
public enum ContractTextStatusEnum implements PullDown {
    NO_SIGNED("未签约"),
    PART_SIGNED("部分签约"),
    SIGNED("已签约"),
    ;

    private final String display;

    public static ContractTextStatusEnum ofName(String name) {
        for (ContractTextStatusEnum status : ContractTextStatusEnum.values()) {
            if (status.name().equals(name)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }
}
