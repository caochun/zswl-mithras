package cn.zswltech.mithras.metric.emit.model.req.relation.trade;

import lombok.Data;

import java.util.List;

/**
 * @author yibin
 */
@Data
public class RelationTradeReqBody {

    private List<RelationTradeBody> relatedTrades;
}
