package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author: jackerhe
 * @date: 2023/6/17 2:11 下午
 **/
@Data
@ApiModel("绩效考核-项目分配-分配比重信息")
public class KpiProjectGuessWeightInfo {
    @ApiModelProperty("id")
    private Long id;

    //占比
    @ApiModelProperty("占比")
    private Integer weightValue;

    private Integer divideWeight;

    //分配比重类型
    @ApiModelProperty("分配比重类型")
    private String divideType;

    //分配比重类型名称
    @ApiModelProperty("分配比重类型名称")
    private String divideTypeName;

    //分配比重目标
    @ApiModelProperty("分配比重目标")
    private Long divideTarget;

    //分配比重目标名称
    @ApiModelProperty("分配比重目标名称")
    private String divideTargetName;

    //利润-当期值
    @ApiModelProperty("利润提奖-当期值")
    private Long profitCurrent;

    //利润-累计值
    @ApiModelProperty("利润提奖-累计值")
    private Long profitTotal;

    @ApiModelProperty("利润提奖-调整值")
    private Long profitAdjust;

    //奖金-当期值
    @ApiModelProperty("奖金-当期值")
    private Long bonusCurrent;

    //奖金-累计值
    @ApiModelProperty("奖金-累计值")
    private Long bonusTotal;


    /**
     * 奖金-调整值
     */
    @ApiModelProperty("奖金-调整值")
    private Long bonusAdjust;

    /**
     * 投放-当期值
     */
    @ApiModelProperty("投放-当期值")
    private Long paymentCurrent;

    /**
     * 投放-累计值
     */
    @ApiModelProperty("投放-累计值")
    private Long paymentTotal;


    /**
     * 投放提奖-当期值
     */
    @ApiModelProperty("投放提奖-当期值")
    private Long paymentAwardCurrent;

    /**
     * 投放提奖-累计值
     */
    @ApiModelProperty("投放提奖-累计值")
    private Long paymentAwardTotal;

    /**
     * 分配年
     */
    @ApiModelProperty("divide_year")
    private Integer divideYear;

    /**
     * 分配月
     */
    @ApiModelProperty("divide_month")
    private Integer divideMonth;

    //基础提奖比例
    @ApiModelProperty("基础提奖比例")
    private String projectRadioConfig;
    //项目规模系数
    @ApiModelProperty("项目规模系数")
    private String scaleRadioConfig;
    //项目类型系数
    @ApiModelProperty("项目类型系数")
    private String typeRadioConfig;
    //投放奖金系数
    @ApiModelProperty("投放奖金系数")
    private String paymentBonusRadioConfig;
    //项目利润当期值
    @ApiModelProperty("项目利润当期值")
    private Long projectProfit;

}
