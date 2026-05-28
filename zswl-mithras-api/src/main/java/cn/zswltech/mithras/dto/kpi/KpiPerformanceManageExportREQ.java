package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yangxiong
 * @date 2024/7/1/20:57
 * @description
 */
@Data
public class KpiPerformanceManageExportREQ {

    @ApiModelProperty(value = "年度")
    @NotNull(message = "年度信息不能为空")
    private Long year;
}
