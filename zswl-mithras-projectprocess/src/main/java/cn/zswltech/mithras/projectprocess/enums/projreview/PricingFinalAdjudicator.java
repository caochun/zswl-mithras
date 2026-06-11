package cn.zswltech.mithras.projectprocess.enums.projreview;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/30 10:52
 */
public enum PricingFinalAdjudicator implements PullDown {
    bizDivisionLeader("业务分管领导"),
    generalManager("总经理"),
    officeSecretary("总经办秘书"),
    ;

    PricingFinalAdjudicator(String display){
        this.display = display;
    }
    public String display;
    @Override
    public String display() {
        return display;
    }
}
