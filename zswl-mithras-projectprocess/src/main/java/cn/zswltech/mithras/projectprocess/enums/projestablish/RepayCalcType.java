package cn.zswltech.mithras.projectprocess.enums.projestablish;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2022/10/27
 * @description
 */
@AllArgsConstructor
@Getter
public enum RepayCalcType implements PullDown {
    /**
     * 等额本息
     */
    DEBX("等额本息"),
    /**
     * 等额本金
     */
    DEBJ("等额本金"),
    /**
     * 到期一次还本付息
     */
    DQYCHBFX("到期一次还本付息"),
    /**
     * 预先付息到期还本
     */
    YXFXDQHB("预先付息到期还本"),
    /**
     * 分期付息到期还本
     */
    QTDQHB("分期付息到期还本"),
//    /**
//     * 到期还本分期结息
//     */
//    DQHBFQJX("到期还本分期结息"),
    /**
     * 其他不规则分期还款
     */
    BGZHK("其他不规则分期还款")
    ;

    private final String display;

    @Override
    public String display() {
        return display;
    }

    public static RepayCalcType find(String name) {
        for (RepayCalcType item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
