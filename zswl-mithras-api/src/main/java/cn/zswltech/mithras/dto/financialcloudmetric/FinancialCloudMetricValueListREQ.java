package cn.zswltech.mithras.dto.financialcloudmetric;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @description 金融云指标
 * @author zhaozhengkang
 * @date 2023-04-12
 */
@Data
@ApiModel("金融云指标列表-请求体")
public class FinancialCloudMetricValueListREQ extends PageReq {
    @ApiModelProperty("指标名称")
    private String metricName;
    @ApiModelProperty(value = "数据日期", required = false)
    @NotNull
    private LocalDate dataTime;
    @ApiModelProperty(value = "一级分类")
    private String metricFirstType;
    @ApiModelProperty(value = "二级分类")
    private String metricSecondType;
    @ApiModelProperty(value = "报送频率")
    private String frequency;
}
