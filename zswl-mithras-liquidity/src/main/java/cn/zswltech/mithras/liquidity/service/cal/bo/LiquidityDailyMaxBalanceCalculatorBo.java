package cn.zswltech.mithras.liquidity.service.cal.bo;

import cn.zswltech.mithras.dto.liquiditymanage.liquidityindex.LiquidityBoardDetailRSP;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

/**
 * LiquidityDailyMaxBalanceCalculatorBo
 *
 * @author chenyifei
 * @since 2024/12/18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiquidityDailyMaxBalanceCalculatorBo extends LiquidityBaseCalculatorBo{

    /**
     * 预测时间
     */
    private LocalDate queryDate;

    /**
     * 预测天数
     */
    private Integer predictDay;

    /**
     * 前置计算的值
     */
    private Map<LocalDate ,LiquidityBoardDetailRSP> detailMap;

}
