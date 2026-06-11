package cn.zswltech.mithras.kpi.dto.persistence;

import lombok.Data;

/**
 * @ClassName KpiProjGuessContractDTO
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/6/15 7:44 下午
 * @Version 1.0
 **/
@Data
public class KpiProjGuessProjCompletionDTO {

    //核算月份
    private String calculateDate;

    //分润-当期值
    private Long profitCurrent;

    //分润-累计值
    private Long profitTotal;

    //主办奖金-当期值-本年存量
    private Long bonusCurrent;

    //主办投放-当期值-本年存量
    private Long paymentCurrent;

    //协办奖金-当期值-本年存量
    private Long bonusCurrentDeputy;

    //协办投放-当期值-本年存量
    private Long paymentCurrentDeputy;

    //主办奖金-当期值-本年新增
    private Long bonusCurrentAdd;

    //主办投放-当期值-本年新增
    private Long paymentCurrentAdd;

    //协办奖金-当期值-本年新增
    private Long bonusCurrentDeputyAdd;

    //主办投放-当期值-本年新增
    private Long paymentCurrentDeputyAdd;

    /**
     * 分配年
     */
    private Integer divideYear;

    /**
     * 分配月
     */
    private Integer divideMonth;
}
