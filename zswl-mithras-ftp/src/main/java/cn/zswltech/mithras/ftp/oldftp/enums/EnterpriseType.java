package cn.zswltech.mithras.ftp.oldftp.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/9 14:17
 */
public enum EnterpriseType implements PullDown {
    //国有企业
    STATE_OWNED_ENTERPRISE("国有企业"),
    // 民营上市企业
    PRIVATE_LISTED("民营上市企业"),
    //民营非上市企业
    PRIVATE_NON_LISTED("民营非上市企业"),
    //其他
    OTHER_SMALL_AND_MICRO("其他小微企业"),

    STATE_OWNED("国有"),
    STATE_OWNED_AND_LISTED("国有/上市公司"),

    OTHER("其他")
    ;


    private String display;

    EnterpriseType(String display){
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
