package cn.zswltech.mithras.metric.emit.model.rsp.risk.index;

import cn.zswltech.mithras.metric.emit.model.req.risk.index.RiskIndexReqSingleBody;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yibin
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RiskIndexRspSingleBody extends RiskIndexReqSingleBody {
    private String error;
}
