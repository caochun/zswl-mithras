package cn.zswltech.mithras.dto.financialcloudmetric;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 金融云指标
 * @author zhaozhengkang
 * @date 2023-04-12
 */
@Data
@ApiModel("金融云指标编辑-请求体")
public class FinancialCloudMetricValueModifyREQ {
    @NotNull
    private Long id;
    @NotNull
    @ApiModelProperty("指标值")
    private String metricValueAdjusted;
}
