package cn.zswltech.mithras.report.enums.biz;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.config.enumscan.PullDownExt;
import cn.zswltech.mithras.service.enums.projestablish.RepayCalcType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 */

@AllArgsConstructor
@Getter
@PullDownExt("crRepayCalcType")
public enum RepayCalcTypeEnum implements PullDown {
    /**
     * 等额本息
     */
    DEBX("11","等额本息"),
    /**
     * 等额本金
     */
    DEBJ("12","等额本金"),
    /**
     * 到期一次还本付息
     */
    DQYCHBFX("21","到期一次还本付息"),
    /**
     * 预先付息到期还本
     */
    YXFXDQHB("22","预先付息到期还本"),
    /**
     * 到期还本分期结息
     */
    QTDQHB("13","到期还本分期结息"),
//    /**
//     * 到期还本分期结息
//     */
//    DQHBFQJX("到期还本分期结息"),
    /**
     * 其他不规则分期还款
     */
    BGZHK("19","其他类型分期还款");

    private final String value;
    private final String display;

    @Override
    public String display() {
        return display;
    }

    @Override
    public String valueKey() {
        return value;
    }

    public static RepayCalcTypeEnum find(String name) {
        for (RepayCalcTypeEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public static RepayCalcTypeEnum findByValue(String value) {
        for (RepayCalcTypeEnum item : values()) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        return null;
    }
}