package cn.zswltech.mithras.service.enums.afterlease;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public enum AfterLeaseCheckStatusEnum implements PullDown {

    UN_P_UN_S("未打卡未提交","1"),
    NOT_S("已打卡未提交","2"),
    P_S("已打卡已提交","3");

    private final String display;

    private final String statusCode;

    @Override
    public String display() {
        return this.display;
    }

    public static AfterLeaseCheckStatusEnum find(String statusCode) {
        for (AfterLeaseCheckStatusEnum item : values()) {
            if (Objects.equals(item.getStatusCode(), statusCode)) {
                return item;
            }
        }
        return null;
    }
}
