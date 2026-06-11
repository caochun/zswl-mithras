package cn.zswltech.mithras.afterlease.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2022/11/9
 * @description
 */
@AllArgsConstructor
@Getter
public enum AfterLeaseCheckPlanStatusEnum implements PullDown {
    NEW("新建"),
    PUBLISH("已发布"),
    MODIFY("变更"),
    CHECKING("检查中"),
    FINISH("计划完结"),
    CLOSE("关闭")
    ;

    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static AfterLeaseCheckPlanStatusEnum find(String name) {
        for (AfterLeaseCheckPlanStatusEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
