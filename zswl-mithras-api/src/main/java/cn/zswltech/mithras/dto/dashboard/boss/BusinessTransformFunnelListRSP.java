package cn.zswltech.mithras.dto.dashboard.boss;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangxiong
 * @date 2024/5/15/17:22
 * @description
 */
@Data
public class BusinessTransformFunnelListRSP {

    @ApiModelProperty(value = "阶段")
    private String stage;

    @ApiModelProperty(value = "历史合计")
    private ValueUnitDTO total;

    @ApiModelProperty(value = "历史转化率")
    private ValueUnitDTO conversionRate;

    @ApiModelProperty(value = "本年新增")
    private ValueUnitDTO incrementThisYear;

    @ApiModelProperty(value = "本年新增转化率")
    private ValueUnitDTO incrementConversionRateThisYear;
}
