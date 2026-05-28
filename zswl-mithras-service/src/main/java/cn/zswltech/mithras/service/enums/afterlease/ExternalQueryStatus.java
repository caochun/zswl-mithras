package cn.zswltech.mithras.service.enums.afterlease;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

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
