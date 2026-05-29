package cn.zswltech.mithras.service.enums.monthly;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/7/25/17:46
 * @description
 */
@Getter
@AllArgsConstructor
public enum MonthlyManagementStatusEnum implements PullDown {

    NOT_CONFIRM("未确认"),
    CLOSED_AMOUNT("已关账"),
    CONFIRMED("已确认"),
    ;
    private final String display;

    @Override
    public String display() {
        return display;
    }
}
