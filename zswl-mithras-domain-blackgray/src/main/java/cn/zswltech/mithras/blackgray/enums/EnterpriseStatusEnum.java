package cn.zswltech.mithras.blackgray.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/12/11 15:00
 */
public enum EnterpriseStatusEnum implements PullDown {
    /**
     * 未命中
     */
    NOT_HIT("未命中"),
    /**
     * 灰名单
     */
    GRAY_LIST("灰名单"),
    /**
     * 黑名单
     */
    BLACK_LIST("黑名单"),
    /**
     * 关注企业
     */
    CONCERN_LIST("关注企业"),
    ;

    public final String display;
    EnterpriseStatusEnum(String display) {
        this.display = display;
    }


    @Override
    public String display() {
        return this.display;
    }
}
