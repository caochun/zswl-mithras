package cn.zswltech.mithras.service.enums.kpi;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.common.enums.PullDown;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/2/13
 * @description 项目提奖比例参数值枚举
 */
@Getter
public enum EclOuterLevelEnum implements PullDown {
    AAA("Aaa", 1),
    AA1("Aa1", 2),
    AA2("Aa2", 3),
    AA3("Aa3", 4),
    A1("A1", 5),
    A2("A2", 6),
    A3("A3", 7),
    BAA1("Baa1", 8),
    BAA2("Baa2", 9),
    BAA3("Baa3", 10),
    BA1("Ba1", 11),
    BA2("Ba2", 12),
    BA3("Ba3", 13),
    B1("B1", 14),
    B2("B2", 15),
    B3("B3", 16),
    CAA1("Caa1", 17),
    CAA2("Caa2", 18),
    CAA3("Caa3", 19),
    CA_C("Ca-C", 20),
    D("D", 21);
    ;

    private final String display;

    private final int order;

    EclOuterLevelEnum(String display, int order) {
        this.display = display;
        this.order = order;
    }

    public static EclOuterLevelEnum getByDisplay(String display) {
        for(EclOuterLevelEnum eunm : EclOuterLevelEnum.values()) {
            if (ObjectUtil.equals(eunm.display, display)) {
                return eunm;
            }
        }
        return null;
    }


    @Override
    public String display() {
        return display;
    }
}
