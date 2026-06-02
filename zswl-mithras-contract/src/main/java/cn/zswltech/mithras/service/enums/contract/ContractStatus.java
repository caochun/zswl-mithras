package cn.zswltech.mithras.service.enums.contract;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @author
 */
public enum ContractStatus implements PullDown {

    NEW("新建"), CLOSED("已关闭"), INVALID("作废"), SETTLE("结清"), START_RENT("起租"), TAKE_EFFECT("生效");

    ContractStatus(String display) {
        this.display = display;
    }

    public final String display;

    public static ContractStatus of(String code) {
        for (ContractStatus value : ContractStatus.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }
    public static ContractStatus find(String name) {
        for (ContractStatus item : values()) {
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
