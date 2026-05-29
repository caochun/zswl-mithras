package cn.zswltech.mithras.service.enums.contract;

import cn.zswltech.mithras.common.enums.PullDown;

/**
 * @author
 */
public enum ProjItemStatus implements PullDown {

    NEW("新建"), CLOSED("已关闭"), INVALID("作废"), SETTLE("结清"), START_RENT("起租"), TAKE_EFFECT("生效");

    public final String display;

    ProjItemStatus(String display) {
        this.display = display;
    }

    public static ProjItemStatus of(String code) {
        for (ProjItemStatus value : ProjItemStatus.values()) {
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
