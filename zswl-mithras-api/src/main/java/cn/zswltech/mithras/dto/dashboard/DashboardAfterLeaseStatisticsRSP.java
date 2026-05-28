package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class DashboardAfterLeaseStatisticsRSP {
    @ApiModelProperty("分组")
    private String group;
    @ApiModelProperty("分组Code")
    private String groupCode;
    @ApiModelProperty("左侧数量")
    private Integer leftCount;
    @ApiModelProperty("右侧数量")
    private Integer rightCount;
}
