package cn.zswltech.mithras.service.enums.riskcontrol;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/16 14:45
 */
public enum MetricUnit {
    /**
     * 倍
     */
    TIMES("倍"),
    /**
     * 百分比%
     */
    PERCENTAGE("%"),
    /**
     * 亿元
     */
    BILLION_YUAN("亿元"),
    /**
     * 年
     */
    YEAR("年"),
    ;
    private String display;

    MetricUnit(String display) {
        this.display = display;
    }

    public String display() {
        return display;
    }


}
