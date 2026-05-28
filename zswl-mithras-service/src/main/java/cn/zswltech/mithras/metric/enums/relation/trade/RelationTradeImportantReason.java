package cn.zswltech.mithras.metric.enums.relation.trade;

/**
 * @author yibin
 */
public enum RelationTradeImportantReason {


    /**
     * 单笔交易金额达到五亿元以上
     */
    GREATER_THAN_500_MILLION(21),
    /**
     * 单笔交易金额占集团金融板块上一年度末经审计的净资产的1%以上
     */
    GREATER_THAN_1_PERCENT(22),

    /**
     * 该笔交易发生后，与一个关联方的年度累计交易金额占集团金融板块上一年度末经审计的净资产5%以上
     */
    GREATER_THAN_5_PERCENT(23);

    public final Integer value;

    RelationTradeImportantReason(int value) {
        this.value = value;
    }

}
