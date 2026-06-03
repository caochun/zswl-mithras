package cn.zswltech.mithras.payment.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/12/11
 * @description
 */
@AllArgsConstructor
@Getter
public enum PaymentStatusEnum implements PullDown {
    NEW("新建"),
    CLOSED("关闭"),
    TAKE_EFFECT("生效"),
    FINISHED("已投放");

    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static PaymentStatusEnum find(String name) {
        for (PaymentStatusEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }
}
