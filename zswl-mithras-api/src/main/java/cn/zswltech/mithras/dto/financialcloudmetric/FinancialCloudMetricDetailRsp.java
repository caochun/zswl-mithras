package cn.zswltech.mithras.dto.financialcloudmetric;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/8/21 14:07
 */
@ApiModel("金融云指标详情")
@Data
public class FinancialCloudMetricDetailRsp {

    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty(value = "metric_id")
    private Long metricId;
    @ApiModelProperty(value = "metric_code")
    private String metricCode;
    @ApiModelProperty(value = "一级分类")
    private String oneLevelType;
    @ApiModelProperty(value = "二级分类")
    private String twoLevelType;
    @ApiModelProperty("指标大类")
    private String metricFirstType;
    @ApiModelProperty("指标小类")
    private String metricSecondType;
    @ApiModelProperty(value = "指标名称")
    private String metricName;
    @ApiModelProperty(value = "当期值")
    private String metricValue;
    @ApiModelProperty(value = "调整后的指标值")
    private String metricValueAdjusted;
    @ApiModelProperty(value = "数据日期")
    private LocalDate dataTime;
    @ApiModelProperty(value = "状态")
    private String status;
    @ApiModelProperty(value = "报送频率")
    private String frequency;
    @ApiModelProperty(value = "单位")
    private String unit;
    @ApiModelProperty(value = "参与计算合同信息")
    private List<ContractDetail> contractDetailList;
}
