package cn.zswltech.mithras.metric.emit.model.req.relation.trade;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
public class RelationTradeBody {
    /**
     * 关联交易金额（万）
     * 小数点前最大15位，保留两位小数
     * 必须
     */
    private BigDecimal amount;
    /**
     * 交易概述
     * 必须
     */
    private String description;

    /**
     * 重大交易原因 字典值。当交易级别为重大交易时，此字段必须。
     */
    private Integer importantReason;

    /**
     * 关联交易级别类型 字典值
     * 必须
     */
    private Integer level;

    /**
     * 董事会决议、关联交易控制委员会的意见或决议
     * 非必须
     */
    private String opinion;

    /**
     * 交易目的
     * 必须
     */
    private String purpose;

    /**
     * 本次交易风险提示，以及对财务状况、经营成果的影响
     * 非必须
     */
    private String risk;


    /**
     * 关联交易一级类型名称，字典值
     * 必须
     */
    private String tradeCategoryParentName;

    /**
     * 关联交易二级类型名称，字典值
     * 必须
     */
    private String tradeCategoryName;

    /**
     * 交易时间
     * 必须
     * yyyy-MM-dd格式
     */
    private LocalDate tradeDate;

    /**
     * 交易对手上一年度末审计净资产（万）
     * 非必须
     */
    private BigDecimal tradePartyAssets;

    /**
     * 关联交易对手名称
     * 必须
     */
    private String tradePartyName;

    /**
     * 关联交易主体机构名称
     * 必须
     */
    private String subjectPartyName;

}
