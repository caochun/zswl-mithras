package cn.zswltech.mithras.dto.metric.factor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author yibin
 */
@Data
@ApiModel("风险指标因子-列表-结果")
public class RiskMetricFactorFileListRsp {

    @ApiModelProperty("记录id")
    private Long id;
    @ApiModelProperty("sheet-日期，传月份即可，日期调整到本月最后一日")
    private LocalDate sheetDate;
    @ApiModelProperty("因子-sheet名称. e.g: 现金流量表")
    private String sheetName;
    @ApiModelProperty("导入时间")
    private LocalDateTime updateTime;
    @ApiModelProperty("文件id")
    private Long fileId;

}
