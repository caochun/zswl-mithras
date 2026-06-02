package cn.zswltech.mithras.ftp.newftp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/3/26/21:00
 * @description 企业类型
 */
@Getter
@AllArgsConstructor
public enum EnterpriseTypeEnum {

    /**
     * 国有企业
     * @deprecated V1版本的FTP使用，已过时，为了兼容历史数据保留枚举项
     */
    @Deprecated
    STATE_OWNED_ENTERPRISE("国有企业"),
    /**
     * 上市企业
     * @deprecated V1版本的FTP使用，已过时，为了兼容历史数据保留枚举项
     */
    @Deprecated
    LISTED_COMPANY("上市企业"),
    CUSTOMER_LISTED_STATE_OWNED("上市公司/国有企业"),
    CUSTOMER_OTHER_LISTED("其他上市公司"),
    /**
     * 其他
     */
    OTHER("其他企业");

    private final String display;
}
