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
public class KpiProjGuessProjManageCompletionIndexRSP {

    //核算月份
    @ApiModelProperty("核算月份")
    private String calculateDate;

    //分润-当期值
    @ApiModelProperty("利润-当期值")
    private Long profitCurrent;

    //分润-累计值
    @ApiModelProperty("利润-累计值")
    private Long profitTotal;

    //奖金-当期值
    @ApiModelProperty("主办奖金-当期值-本年存量")
    private Long bonusCurrent;

    //奖金-累计值
    @ApiModelProperty("主办投放-当期值-本年存量")
    private Long paymentCurrent;

    //奖金-当期值
    @ApiModelProperty("协办奖金-当期值-本年存量")
    private Long bonusCurrentDeputy;

    //奖金-累计值
    @ApiModelProperty("主办投放-当期值-本年存量")
    private Long paymentCurrentDeputy;

    //奖金-当期值
    @ApiModelProperty("主办奖金-当期值-本年新增")
    private Long bonusCurrentAdd;

    //奖金-累计值
    @ApiModelProperty("主办投放-当期值-本年新增")
    private Long paymentCurrentAdd;

    //奖金-当期值
    @ApiModelProperty("协办奖金-当期值-本年新增")
    private Long bonusCurrentDeputyAdd;

    //奖金-累计值
    @ApiModelProperty("主办投放-当期值-本年新增")
    private Long paymentCurrentDeputyAdd;

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
