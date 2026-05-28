package cn.zswltech.mithras.dto.dashboard.boss;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangxiong
 * @date 2024/5/15/15:01
 * @description
 */
@Data
public class DashboardListRSP {

    @ApiModelProperty(value = "看板Key")
    private String dashboardKey;

    @ApiModelProperty(value = "看板描述")
    private String dashboardDisplay;

    @ApiModelProperty(value = "数据截止日期")
    private String dataDate;
}
