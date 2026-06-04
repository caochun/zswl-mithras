package cn.zswltech.mithras.projectprocess.enums.projestablish;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @author junke
 */
public enum ProjEstablishApprovalType implements PullDown {
    SIMPLE("简易审批"), NORMAL("一般审批");

    ProjEstablishApprovalType(String display) {
        this.display = display;
    }

    public String display;


    @Override
    public String display() {
        return display;
    }
}
