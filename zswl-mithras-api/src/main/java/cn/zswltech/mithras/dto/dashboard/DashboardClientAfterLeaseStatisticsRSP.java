package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@Data
public class DashboardClientAfterLeaseStatisticsRSP {
    @ApiModelProperty("分组")
    private String group;
    @ApiModelProperty("分组Code")
    private String groupCode;
    @ApiModelProperty("左侧数量")
    private Integer leftCount;
    @ApiModelProperty("右侧数量")
    private Integer rightCount;
}
