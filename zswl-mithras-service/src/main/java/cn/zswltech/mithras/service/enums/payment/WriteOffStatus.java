package cn.zswltech.mithras.service.enums.payment;

import cn.zswltech.mithras.common.enums.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/18 09:51
 */
public enum WriteOffStatus implements PullDown {
    TO_BE_WRITE_OFF("未提交"),
    COMMIT("审批中"),
    CONFIRM("已确认"),
    WRITTEN_OFF("已核销"),
    CLOSED("已关闭"),
    IGNORE("忽略"),
    ;
    public String display;
    WriteOffStatus(String display){
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
