package cn.zswltech.mithras.workflow.enums.trackevent;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务来源
 */

@AllArgsConstructor
@Getter
public enum TrackTaskBizSourceEnum implements PullDown {

    CONTRACT("合同",1),
    PROJ_REVIEW("项目评审",2),
    AFTER_LEASE("租后检查",3),
    PAYMENT("付款申请",4),
    LEDGER("台账",5),
    ;


    public final String display;
    public final int sort;

    @Override
    public String display() {
        return display;
    }


    public static TrackTaskBizSourceEnum find(String name) {
        for (TrackTaskBizSourceEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
