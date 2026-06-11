package cn.zswltech.mithras.workflow.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流程对应节点名称
 */
@AllArgsConstructor
@Getter
public enum FlowNodeEnum implements PullDown {

    userTask_startUser("发起人意见"),
    userTask_loanReviewPost("放款审核岗意见"),
    Activity_0wrrxch("运营管理岗意见");

    public final String display;

    public static FlowNodeEnum of(String name) {
        for (FlowNodeEnum value : FlowNodeEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }

}
