package cn.zswltech.mithras.dto.dashboard.boss;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangxiong
 * @date 2024/5/15/16:56
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistributionClientStatisticsListRSP {

    @ApiModelProperty(value = "类型名称")
    private String name;

    @ApiModelProperty(value = "总数量")
    private Integer totalCount;

    @ApiModelProperty(value = "本月新增数量")
    private Integer newCountThisMonth;

    @ApiModelProperty(value = "类型key（跳转用）")
    private String type;
}
