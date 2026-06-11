package cn.zswltech.mithras.afterlease.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/11/19 11:41
 */
public enum ExternalQueryStatus implements PullDown {
    TO_BE_QUERY("待查询"),
    UNDER_APPROVAL("审批中"),
    APPROVAL_PASS("审批通过"),
    APPROVAL_REJECT("审批拒绝")
    ;

    ExternalQueryStatus(String display){
        this.display = display;
    }
    private String display;
    @Override
    public String display() {
        return display;
    }
}
