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
public class KpiProjGuessProjDTO {

    //核算月份
    private String calculateDate;

    //分润-当期值
    private Long profitCurrent;

    //分润-累计值
    private Long profitTotal;

    //奖金-当期值
    private Long bonusCurrent;

    //奖金-累计值
    private Long paymentCurrent;

    //协办奖金-当期值
    private Long bonusCurrentDeputy;

    //协办奖金-累计值
    private Long paymentCurrentDeputy;

    /**
     * 分配年
     */
    private Integer calculateDateYear;

    /**
     * 分配月
     */
    private Integer calculateDateMonth;

}
