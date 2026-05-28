package cn.zswltech.mithras.dto.finance.accountage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description 帐龄主表
 * @author vico
 * @date 2024-09-10
 */
@Data
@ApiModel("帐龄主表详情-返回体")
public class FinanceAccountAgeCountRSP {

    /**
     * 期初款项原值
     */
    @ApiModelProperty(value = "期初款项原值")
    private BigDecimal originalValueInitial;

    /**
     * 本期增加额 >0增加 <0减少
     */
    @ApiModelProperty(value = "本期增加额 ")
    private BigDecimal originalValueIncrease;

    @ApiModelProperty(value = "本期减少额 ")
    private BigDecimal originalValueReduce;

    /**
     * 期末款项原值
     */
    @ApiModelProperty(value = "期末款项原值")
    private BigDecimal originalValueFinal;

}
