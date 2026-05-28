package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/27
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectPayListREQ extends DashboardProjectPayStatisticsREQ {
    @ApiModelProperty("客户ID")
    private Long clientId;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("投放日期-起")
    private LocalDate actualPayDateFrom;
    @ApiModelProperty("投放日期-止")
    private LocalDate actualPayDateTo;
    @ApiModelProperty("统计维度，合同或者借据 @link PayInfoQueryDimensionEnum")
    @NotBlank(message = "统计维度不能为空")
    private String queryDimension;
}
