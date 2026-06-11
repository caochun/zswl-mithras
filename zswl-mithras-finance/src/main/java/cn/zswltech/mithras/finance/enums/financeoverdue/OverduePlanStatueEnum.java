package cn.zswltech.mithras.finance.enums.financeoverdue;

import cn.zswltech.mithras.foundation.metadata.PullDown;

public enum OverduePlanStatueEnum implements PullDown {
    NEW("新建未报送"), FINISHED("已完成报送"), CLOSE("关闭"), PART("部分报送");

    OverduePlanStatueEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static OverduePlanStatueEnum of(String code) {
        for (OverduePlanStatueEnum value : OverduePlanStatueEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return this.display;
    }
}
