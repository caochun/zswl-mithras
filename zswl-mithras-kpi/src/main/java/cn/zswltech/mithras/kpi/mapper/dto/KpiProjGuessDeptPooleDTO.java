package cn.zswltech.mithras.kpi.mapper.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName KpiProjGuessContractDTO
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/6/15 7:44 下午
 * @Version 1.0
 **/
@Data
public class KpiProjGuessDeptPooleDTO {



    /**
     * 分配年
     */
    private Integer divideYear;

    /**
     * 分配月
     */
    private Integer divideMonth;


    //利润-当期值
    private Long profitTotal;

    //奖金-当期值
    private Long bonusTotal;

    //奖金-累计值
    private Long paymentTotal;

    //投放-累计值
    private Long paymentTotalAmount;

    //奖金-累计值
    @ApiModelProperty("合计奖金-部门池")
    private Long amount;

}
