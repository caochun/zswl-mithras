package cn.zswltech.mithras.contract.enums.contract;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2022/11/1
 * @description
 */
@Getter
@AllArgsConstructor
public enum ContractAccountUseEnum implements PullDown {
    ZLSK("租赁收款"),
    ZZSK("转租赁收款"),
    BLHK("保理回款"),
    BLSK("保理收款"),
    ZRHK("债权转让回款"),
    ZRSK("债权转让收款")
    ;

    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static ContractAccountUseEnum find(String name) {
        for (ContractAccountUseEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
