package cn.zswltech.mithras.service.enums.lease;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  租赁物审核状态枚举
 * @author yangxiong
 * @since 2023-09-25
 * @deprecated 复用cn.zswltech.mithras.service.enums.common.ProcessStatus
 */

@Deprecated
@Getter
@AllArgsConstructor
public enum LeaseAuditStatusEnum implements PullDown {
    /**
     * 审核状态
     */
    AUDITING("审批中"),
    CHANGING("变更中"),
    AUDITED("审批通过"),
    CLOSED("已关闭");

    private final String display;

    @Override
    public String display() {
        return display;
    }

    @Override
    public String valueKey() {
        return PullDown.super.valueKey();
    }
}
