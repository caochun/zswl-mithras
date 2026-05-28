package cn.zswltech.mithras.dto.metric.value;

import cn.zswltech.mithras.api.common.PageR;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author yibin
 */
@Data
public class RiskMetricValueListRsp {

    @ApiModelProperty("最近报送时间")
    private LocalDateTime lastReportTime;

    @ApiModelProperty("报送数据分页列表")
    private PageR<RiskMetricValueListSingle> dataList;

    @Data
    public static class RiskMetricValueListSingle {

        @ApiModelProperty("风险指标值id")
        private Long id;

        @ApiModelProperty("风险指标模板id")
        private Long riskMetricId;

        @ApiModelProperty("指标名称")
        private String metricName;

        @ApiModelProperty("指标编号")
        private String metricCode;

        @ApiModelProperty("单位")
        private String unit;

        @ApiModelProperty("报送频率")
        private String frequency;


        @ApiModelProperty("风险指标值")
        private Long metricValue;

        @ApiModelProperty("风险指标值-调整后的(如有")
        private Long metricValueAdjusted;

        /**
         * 数据日期
         */
        @ApiModelProperty("风险指标数据日期")
        private LocalDate dataTime;

        @ApiModelProperty("是否需要报送")
        private Boolean needReport;

        /**
         * 数据来源
         */
        @ApiModelProperty("风险指标值来源")
        private String dataSource;

        @ApiModelProperty("状态")
        private String status;

        @ApiModelProperty("创建时间")
        private LocalDateTime createTime;
    }
}
