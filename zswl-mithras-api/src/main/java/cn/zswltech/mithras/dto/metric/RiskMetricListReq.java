package cn.zswltech.mithras.dto.metric;

import cn.zswltech.mithras.dto.PageReq;
import lombok.Data;

/**
 * @author yibin
 */
@Data
public class RiskMetricListReq extends PageReq {
    private String metricName;

    private String metricCode;


}
