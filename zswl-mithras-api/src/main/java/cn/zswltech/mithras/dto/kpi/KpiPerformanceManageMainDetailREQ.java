package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yangxiong
 * @date 2024/7/3/16:04
 * @description
 */
@Data
public class KpiPerformanceManageMainDetailREQ {

    @ApiModelProperty("主键ID")
    @NotNull(message = "主键ID不能为空")
    private Long id;
}
