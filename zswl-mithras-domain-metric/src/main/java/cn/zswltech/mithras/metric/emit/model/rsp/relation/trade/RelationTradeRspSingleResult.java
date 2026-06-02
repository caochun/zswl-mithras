package cn.zswltech.mithras.metric.emit.model.rsp.relation.trade;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
public class RelationTradeRspSingleResult {

    private BigDecimal amount;
    private String statusCode;
    private String statusMsg;

    private LocalDate tradeDate;
    private String tradePartyName;
    private String subjectPartyName;

}
