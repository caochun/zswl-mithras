package cn.zswltech.mithras.contract.enums.contract;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2022/11/1
 * @description
 */
@Getter
@AllArgsConstructor
public enum ContractBizTypeEnum implements PullDown {
    BLWZ("保理无追合同"),
    BLYZ("保理有追合同"),
    ZLHZ("租赁回租合同"),
    ZLJY("租赁经营合同"),
    ZLZZ("租赁直租合同"),
    ;

    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static ContractBizTypeEnum find(String name) {
        for (ContractBizTypeEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
