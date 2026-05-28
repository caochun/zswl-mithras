package cn.zswltech.mithras.dto.metric.factor;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
public class RiskMetricFactorImportRemoveReq {
    @NotNull
    private LocalDate factorDate;
    private String factorTable;
}
