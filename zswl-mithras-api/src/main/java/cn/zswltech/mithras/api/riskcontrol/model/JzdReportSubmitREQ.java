package cn.zswltech.mithras.api.riskcontrol.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
public class JzdReportSubmitREQ {
    @ApiModelProperty("月份")
    @NotNull
    private LocalDate dataMonth;
}
