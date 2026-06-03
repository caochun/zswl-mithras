package cn.zswltech.mithras.archives.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @create: 2023-02-27
 **/
public enum ArchivesFlowStatusEnum implements PullDown {
    NEW_UN_SUBMIT("自发归档创建未提交"),
    UNDER_APPROVAL("自发归档审批中"),
    APPROVAL_PASS("自发归档审批通过"),
    APPROVAL_CLOSE("自发归档审批关闭"),
    REJECT("自发归档审批拒绝"),
    SYS_NEW_UN_SUBMIT("归档任务未提交"),
    SYS_UNDER_APPROVAL("归档任务审批中"),
    SYS_APPROVAL_PASS("归档任务审批通过"),
    SYS_APPROVAL_CLOSE("归档任务审批关闭"),

    SYS_REJECT("归档任务审批拒绝")
;
    ArchivesFlowStatusEnum(String display) {
        this.display = display;
    };

    public final String display;

    public static ArchivesFlowStatusEnum of(String code) {
        for (ArchivesFlowStatusEnum value : ArchivesFlowStatusEnum.values()) {
            if (value.name().equals(code)) {
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
