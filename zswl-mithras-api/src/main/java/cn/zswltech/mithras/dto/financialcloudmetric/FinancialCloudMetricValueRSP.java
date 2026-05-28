package cn.zswltech.mithras.dto.financialcloudmetric;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.metric.value.RiskMetricValueListRsp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: Chen
 * @date: 2023/4/16 16:45
 */
@Data
@ApiModel("金融云指标列表-返回体")
public class FinancialCloudMetricValueRSP {
    @ApiModelProperty("最近报送时间")
    private LocalDateTime lastReportTime;

    @ApiModelProperty("报送数据分页列表")
    private PageR<FinancialCloudMetricValueListRSP> dataList;
}
