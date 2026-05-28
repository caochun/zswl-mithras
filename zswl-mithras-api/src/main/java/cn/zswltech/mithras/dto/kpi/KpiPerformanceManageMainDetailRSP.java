package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangxiong
 * @date 2024/7/3/16:05
 * @description
 */
@Data
public class KpiPerformanceManageMainDetailRSP {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "年度")
    private Long year;

    @ApiModelProperty(value = "状态")
    private Integer status;
}
