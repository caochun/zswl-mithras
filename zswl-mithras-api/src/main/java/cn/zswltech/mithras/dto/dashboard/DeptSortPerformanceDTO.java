package cn.zswltech.mithras.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yangxiong
 * @date 2024/6/24/15:29
 * @description
 */
@Data
@ApiModel(value = "部门内业绩排名响应体")
public class DeptSortPerformanceDTO {

    @ApiModelProperty(value = "绩效基本表ID")
    private Long performanceId;

    @ApiModelProperty(value = "部门ID")
    private Long deptId;

    @ApiModelProperty(value = "部门名称")
    private String deptName;

    @ApiModelProperty(value = "投放达成率")
    private String deliveryAchievementRate;

    @JsonIgnore
    private Double deliveryAchievementRateDouble;

    @ApiModelProperty(value = "投放达成率排名")
    private String deliveryAchievementRateSort;

    @ApiModelProperty(value = "营业收入达成率")
    private String revenueAchievementRate;

    @JsonIgnore
    private Double revenueAchievementRateDouble;

    @ApiModelProperty(value = "营业收入达成率排名")
    private String revenueAchievementRateSort;

    @ApiModelProperty(value = "利润目标达成率")
    private String profitAchievementRate;

    @JsonIgnore
    private Double profitAchievementRateDouble;


    @ApiModelProperty(value = "利润目标达成率排名")
    private String profitAchievementRateSort;
}
