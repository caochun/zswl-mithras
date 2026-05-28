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
public class KpiPerformanceManageListREQ {

    @ApiModelProperty(value = "主键ID")
    @NotNull(message = "主表ID不能为空")
    private Long id;
}
