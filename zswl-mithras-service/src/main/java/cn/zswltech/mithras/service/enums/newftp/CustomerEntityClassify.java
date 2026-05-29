package cn.zswltech.mithras.service.enums.newftp;

import cn.zswltech.mithras.common.enums.PullDown;

/**
 * @description: 客户主体分类
 * @author: zhaozhengkang
 * @date: 2023/5/19 11:24
 */
public enum CustomerEntityClassify implements PullDown {
    /**
     * 国有企业
     * @deprecated V1版本的FTP使用，已过时，为了兼容历史数据保留枚举项
     */
    @Deprecated
    STATE_OWNED_ENTERPRISE("国有企业"),
    /**
     * 上市公司
     * @deprecated V1版本的FTP使用，已过时，为了兼容历史数据保留枚举项
     */
    @Deprecated
    LISTED_COMPANY("上市公司"),

    CUSTOMER_LISTED_STATE_OWNED("上市公司/国有企业"),
    CUSTOMER_OTHER_LISTED("其他上市公司"),

    /**
     * 其他
     */
    OTHER("其他");

    private final String display;

    CustomerEntityClassify(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
