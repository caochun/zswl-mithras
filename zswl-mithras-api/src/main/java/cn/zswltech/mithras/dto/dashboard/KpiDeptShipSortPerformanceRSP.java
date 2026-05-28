package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author yangxiong
 * @date 2024/7/2/23:00
 * @description
 */
@Data
public class KpiDeptShipSortPerformanceRSP {

    @ApiModelProperty(value = "数据更新时间 格式：yyyy-MM-dd")
    private String dataUpdateTime;

    @ApiModelProperty(value = "数据列表")
    private List<DeptShipSortPerformanceRSP> dataList;
}
