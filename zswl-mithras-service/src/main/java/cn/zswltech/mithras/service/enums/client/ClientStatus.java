package cn.zswltech.mithras.service.enums.client;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @author luyi
 */
public enum ClientStatus implements PullDown {
    /**
     * 新建
     */
    NEW("新建"),
    //PEND_TAKE_EFFECT("变更中"),
    TAKE_EFFECT("生效"),
    // 释放的枚举值是给前端用的，数据库存储的时候是client_status = NEW配合is_related = 1来判断
    // 不直接使用该枚举是为了兼容老代码，尽可能减少老代码的改动及出错风险
    RELEASE("释放")
    ;

    ClientStatus(String display) {
        this.display = display;
    }

    public final String display;

    public static ClientStatus of(String code) {
        for (ClientStatus value : ClientStatus.values()) {
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
