package cn.zswltech.mithras.riskcontrol.common;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/8 16:35
 */
public enum MetricType implements PullDown {
    /**
     * 流动性风险指标限额
     */
    LIQUIDITY_RISK("流动性风险指标限额"),
    /**
     * 信用风险指标限额
     */
    CREDIT_RISK("信用风险指标限额"),
    /**
     * 市场风险指标限额
     */
    MARKET_RISK("合规风险指标限额"),
    /**
     * 业务风险指标限额
     */
    BUSINESS_RISK("业务风险指标限额"),
    /**
     * 区域风险限额指标
     * （不含公用事业类、民生消费类、集团协同业务类）
     */
    REGIONAL_RISK("区域风险限额指标（不含公用事业类、民生消费类、集团协同业务类）"),
    /**
     * 资本类指标限额
     */
    CAPITAL("资本类指标限额")
    ;
    private String display;

    MetricType(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
