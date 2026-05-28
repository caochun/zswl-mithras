package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author yangxiong
 * @date 2024/6/24/19:27
 * @description
 */
@Data
@ApiModel(value = "个人业绩响应体")
public class PersonalPerformanceRSP {

    @ApiModelProperty(value = "卡片列表")
    private List<CardListVo> cardList;

    @ApiModelProperty(value = "项目经理ID")
    private Long projManagerId;

    @ApiModelProperty(value = "项目经理名称")
    private String projManagerName;

    @ApiModelProperty(value = "投放达成率")
    private String deliveryAchievementRate;

    @ApiModelProperty(value = "投放达成率排名")
    private Integer deliveryAchievementRateSort;

    @ApiModelProperty(value = "利润目标达成率")
    private String profitAchievementRate;

    @ApiModelProperty(value = "利润目标达成率排名")
    private Integer profitAchievementRateSort;

}
