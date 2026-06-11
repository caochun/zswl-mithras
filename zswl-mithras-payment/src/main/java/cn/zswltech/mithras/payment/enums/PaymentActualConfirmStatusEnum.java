package cn.zswltech.mithras.payment.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/12/11
 * @description
 */
@AllArgsConstructor
@Getter
public enum PaymentActualConfirmStatusEnum implements PullDown {
    WAIT("未提交"),
    SUBMIT("审批中"),
    CLOSED("已关闭"),
    CONFIRMED("已确认");

    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}
