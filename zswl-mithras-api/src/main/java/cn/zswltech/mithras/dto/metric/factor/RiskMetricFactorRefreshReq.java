package cn.zswltech.mithras.dto.metric.factor;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
public class RiskMetricFactorRefreshReq {
    @NotNull
    private LocalDate date;
}
