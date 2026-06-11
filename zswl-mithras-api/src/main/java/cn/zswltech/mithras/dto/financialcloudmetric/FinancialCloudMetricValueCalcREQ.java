package cn.zswltech.mithras.dto.financialcloudmetric;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 金融云指标
 * @date 2023-04-12
 */
@Data
@ApiModel("金融云指标新增-请求体")
public class FinancialCloudMetricValueCalcREQ {
    @NotNull
    @ApiModelProperty(value = "数据时点", required = true)
    private LocalDate dataTime;
}
