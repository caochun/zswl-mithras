package cn.zswltech.mithras.dto.dashboard.boss;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author bigbear
 * @date 2024/10/22 17:27
 * @description
 */
@Data
@ApiModel(value = "业务月度收入统计api请求体")
public class MonthCollectStatisticsListREQ {

    @ApiModelProperty("@link DashBoardQueryTypeEnum#name")
    @NotBlank(message = "查询类型不能为空")
    private String type;

    @ApiModelProperty("日期-起-暂时用不到")
    private String queryDateFrom;

    @ApiModelProperty("日期-止-暂时用不到")
    private String queryDateTo;
}
