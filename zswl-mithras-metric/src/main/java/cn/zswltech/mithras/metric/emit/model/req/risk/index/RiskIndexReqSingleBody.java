package cn.zswltech.mithras.metric.emit.model.req.risk.index;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yibin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskIndexReqSingleBody {
    private String name;
    private String code;
    private String value;
}
