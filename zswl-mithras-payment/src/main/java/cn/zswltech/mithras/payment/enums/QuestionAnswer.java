package cn.zswltech.mithras.payment.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/20 15:38
 */
public enum QuestionAnswer implements PullDown {
    SATISFY("是"),
    DISSATISFACTION("否"),
    PARTIAL_SATISFACTION("不适用");

    public String display;
    QuestionAnswer(String display){
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
