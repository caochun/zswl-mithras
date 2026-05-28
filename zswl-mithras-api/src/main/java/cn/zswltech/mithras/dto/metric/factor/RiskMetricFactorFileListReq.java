package cn.zswltech.mithras.dto.metric.factor;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
@ApiModel("风险指标因子-文件列表-参数")
public class RiskMetricFactorFileListReq extends PageReq {
    @ApiModelProperty(value = "sheet-日期，传月份即可，日期调整到本月最后一日", required = true)
    private LocalDate sheetDate;
    @ApiModelProperty("因子-sheet名称. e.g: 现金流量表")
    private String sheetName;
}
