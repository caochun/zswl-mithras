package cn.zswltech.mithras.kpi.mapper.dto;

import lombok.Data;

/**
 * @ClassName KpiProjGuessContractDTO
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/6/15 7:44 下午
 * @Version 1.0
 **/
@Data
public class KpiProjGuessDeptPooleDetailDTO {

    private Long deptId;

    //分配类型
    private String divideType;

    //分配目标id
    private Long divideTarget;

    //利润-当期值
    private Long profitTotal;

    //奖金-当期值
    private Long bonusTotal;

    //奖金-累计值
    private Long paymentTotal;

    //投放-累计值
    private Long paymentAwardTotal;

}
