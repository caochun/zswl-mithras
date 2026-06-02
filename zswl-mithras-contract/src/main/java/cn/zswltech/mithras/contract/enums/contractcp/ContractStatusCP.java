package cn.zswltech.mithras.contract.enums.contractcp;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @author
 */
public enum ContractStatusCP implements PullDown {

    SETTLE("结清"), START_RENT("起租"), TAKE_EFFECT("生效");

    ContractStatusCP(String display) {
        this.display = display;
    }

    public final String display;

    public static ContractStatusCP of(String code) {
        for (ContractStatusCP value : ContractStatusCP.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }
}
