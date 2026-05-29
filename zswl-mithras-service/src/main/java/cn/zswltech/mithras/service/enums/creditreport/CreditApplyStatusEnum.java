package cn.zswltech.mithras.service.enums.creditreport;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public enum CreditApplyStatusEnum implements PullDown {
    UN_SUBMIT("未提交","UN_SUBMIT"),
    COMMIT("审批中","COMMIT"),
    CLOSE("已关闭","CLOSE"),
    REJECT("审批拒绝","REJECT"),
    PASS("审批通过","PASS"),
    ;

    private final String display;
    /**
     * 申请状态
     */
    private final String name;

    @Override
    public String display() {
        return this.display;
    }

    public static CreditApplyStatusEnum finaByName(String name) {
        for (CreditApplyStatusEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }
}