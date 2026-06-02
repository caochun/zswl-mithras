package cn.zswltech.mithras.metric.emit.model.req.relation.trade;

import lombok.Data;

/**
 * @author yibin
 */
@Data
public class RelatedClientListRSP {
    private String creditCode;
    private String name;
    private Integer partyType;
    private String relationDesc;
}
