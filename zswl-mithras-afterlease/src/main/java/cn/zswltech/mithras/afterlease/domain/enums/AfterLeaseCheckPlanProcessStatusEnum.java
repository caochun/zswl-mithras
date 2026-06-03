package cn.zswltech.mithras.afterlease.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2022/11/15
 * @description
 */
@AllArgsConstructor
@Getter
public enum AfterLeaseCheckPlanProcessStatusEnum implements PullDown {
    NEW_COMMIT("新建审批中"),
    NEW_PASS("新建审批通过"),
    NEW_REJECT("新建审批拒绝"),
    NEW_CANCEL("取消新建"),
    PLAN_ACK_PASS("计划确认通过"),

    MODIFY_UN_COMMIT("变更未提交"),
    MODIFY_COMMIT("变更审批中"),
    MODIFY_PASS("变更审批通过"),
    MODIFY_REJECT("变更审批拒绝"),
    MODIFY_CANCEL("取消变更"),

    FINISH_COMMIT("计划完结审批中"),
    FINISH_PASS("计划完结审批通过"),
    FINISH_REJECT("计划完结审批拒绝"),
    FINISH_CANCEL("取消完结")
    ;

    private final String display;

    public static AfterLeaseCheckPlanProcessStatusEnum of(String code) {
        for (AfterLeaseCheckPlanProcessStatusEnum value : AfterLeaseCheckPlanProcessStatusEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return this.display;
    }
}
