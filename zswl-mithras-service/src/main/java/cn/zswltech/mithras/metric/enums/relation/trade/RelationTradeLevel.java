package cn.zswltech.mithras.metric.enums.relation.trade;

/**
 * @author yibin
 */
public enum RelationTradeLevel {
    NORMAL(1, "一般关联交易"), GREAT(2, "重大关联交易");

    public final int value;
    public final String desc;

    RelationTradeLevel(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
