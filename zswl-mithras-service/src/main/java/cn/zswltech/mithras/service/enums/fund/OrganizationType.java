package cn.zswltech.mithras.service.enums.fund;

import cn.zswltech.mithras.common.enums.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/12/14 10:11
 */
public enum OrganizationType implements PullDown {

    BANK("银行"),
    ZL("租赁公司"),
    OTHER("其他"),
    JT("集团公司");

    private String display;

    OrganizationType(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
