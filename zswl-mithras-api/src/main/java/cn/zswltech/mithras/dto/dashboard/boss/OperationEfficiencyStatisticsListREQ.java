package cn.zswltech.mithras.dto.dashboard.boss;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangxiong
 * @date 2024/5/15/17:31
 * @description
 */
@Data
public class OperationEfficiencyStatisticsListREQ {

    @ApiModelProperty(value = "类型，可为空")
    private String type;
}
