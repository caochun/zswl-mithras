package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName KpiProjGuessContractIndexRSP
 * @Description 项目绩效测算表-合同维度-返回体
 * @Author jackerhe
 * @Date 2023/6/15 4:28 下午
 * @Version 1.0
 **/
@Data
public class KpiProjGuessDeptPoolIndexRSP {

    //核算月份
    @ApiModelProperty("核算月份")
    private String calculateDate;

    //分润-当期值
    @ApiModelProperty("利润-部门池")
    private Long profitTotal;

    //奖金-当期值
    @ApiModelProperty("奖金-部门池")
    private Long bonusTotal;

    //奖金-累计值
    @ApiModelProperty("投放-部门池")
    private Long paymentTotal;

    //奖金-累计值
    @ApiModelProperty("合计奖金-部门池")
    private Long amount;

    /**
     * 分配年
     */
    @ApiModelProperty("分配年")
    private Integer divideYear;

    /**
     * 分配月
     */
    @ApiModelProperty("分配月")
    private Integer divideMonth;
}
