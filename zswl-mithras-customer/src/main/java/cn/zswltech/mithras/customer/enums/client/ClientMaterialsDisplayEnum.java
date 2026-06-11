package cn.zswltech.mithras.customer.enums.client;


import cn.hutool.core.util.StrUtil;

import java.util.Optional;

/**
 * @create: 2022-08-02
 **/
public enum ClientMaterialsDisplayEnum {
    CREDITORCLIENTID("债权人基础资料", 2),
    DEBTORINFO("债务人基础资料", 3),
    LESSEEINFO("承租人基础资料", 4),
    GUARANTEEINFO("担保人基础资料", 5),
    MORTGAGORINFO("抵押人基础资料", 6),
    PLEDGORINFO("质押人基础资料", 7),
    OTHERS("其他", 1),

    GROUPCREDITCLIENTID("授信主体基础资料", 1),
    ;


    ClientMaterialsDisplayEnum(String display, Integer order) {
        this.display = display;
        this.order = order;
    }

    public final String display;
    public final Integer order;

    public static ClientMaterialsDisplayEnum of(String name) {
        for (ClientMaterialsDisplayEnum value : ClientMaterialsDisplayEnum.values()) {
            if (StrUtil.isNotEmpty(name) && name.contains(value.display)) {
                return value;
            }
        }
        return null;
    }

    public static ClientMaterialsDisplayEnum ofWithDefault(String name) {
        return Optional.ofNullable(of(name)).orElse(OTHERS);
    }

}
