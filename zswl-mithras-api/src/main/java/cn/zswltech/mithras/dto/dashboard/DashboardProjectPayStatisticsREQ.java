package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.awt.*;

/**
 * @author dingqi
 * @date 2024/6/23
 * @description
 */
@Data
public class DashboardProjectPayStatisticsREQ {
    public static final String QUERY_TYPE_MONTH = "MONTH";
    public static final String QUERY_TYPE_YEAR = "YEAR";

    @ApiModelProperty("查询类型，MONTH-本月，YEAR-本年")
    @NotBlank(message = "统计维度不能为空")
    private String queryType;
}
