package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2023/6/15
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class KpiProjectDistributionWeightInfoWithTag extends KpiProjectDistributionWeightInfo {
    @ApiModelProperty("分配比重目标名称是否需要标红")
    private Boolean weightTargetNameRed = Boolean.FALSE;

    @ApiModelProperty("分配比重目标值是否需要标红")
    private Boolean weightValueRed = Boolean.FALSE;
}
