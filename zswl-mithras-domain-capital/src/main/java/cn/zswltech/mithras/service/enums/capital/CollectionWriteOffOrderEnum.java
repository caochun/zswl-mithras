package cn.zswltech.mithras.service.enums.capital;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/5/27/15:15
 * @description 现金流核销顺序
 */
@Getter
@AllArgsConstructor
public enum CollectionWriteOffOrderEnum implements PullDown {
    EARNEST_MONEY("保证金", 1),
    FIRST_RENT("首期租金", 3),
    COMMISSION("手续费", 6),
    FIRST_INSTALLMENT_INTEREST("首期利息", 4),
    RETENTION_MONEY("质保金", 2),
    OTHERAMOUNT("服务费/咨询费", 5),
    PRINCIPAL("本金", 7),
    INTEREST("利息", 8),
    PENALTY_INTEREST("罚息", 9),
    EARLY_STOP_COMPENSATION("提前终止补偿金", 10),
    NOMINAL_PRICE("名义价款", 11),
    ;

    private final String display;
    private final Integer sort;

    public static CollectionWriteOffOrderEnum of(String name) {
        for (CollectionWriteOffOrderEnum value : CollectionWriteOffOrderEnum.values()) {
            if (value.name().equals(name)) {
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
