package cn.zswltech.mithras.service.enums.contract;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 质押类型
 *
 * @author yupengfei
 * @date 2024/6/18 11:19
 */
@AllArgsConstructor
@Getter
public enum PledgeTypeEnum implements PullDown {

    EQUITY_PLEDGE("股权质押"),
    ACCOUNTS_RECEIVABLE_PLEDGE("应收账款质押"),
    OTHER_PLEDGE("其他质押"),
    ;

    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static PledgeTypeEnum findByName(String name) {
        for (PledgeTypeEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }
}
