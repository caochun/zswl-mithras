package cn.zswltech.mithras.metric.emit.model.req.risk.index;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yibin
 */
@Data
public class RiskIndexReqBody {
    private LocalDateTime dataTimeBegin;
    private LocalDateTime dataTimeEnd;
    private String frequency;
    private List<RiskIndexReqSingleBody> indexList;
    private String orgCode;
}
