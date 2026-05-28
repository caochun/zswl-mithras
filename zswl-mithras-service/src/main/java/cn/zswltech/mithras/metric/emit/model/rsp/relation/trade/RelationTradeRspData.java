package cn.zswltech.mithras.metric.emit.model.rsp.relation.trade;

import lombok.Data;

import java.util.List;

/**
 * @author yibin
 */
@Data
public class RelationTradeRspData {
    private Integer failCount;
    private Integer successCount;
    private List<RelationTradeRspSingleResult> resultList;

}
