package cn.zswltech.mithras.liquidity.service.cal.bo;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * LiquidityBoardCalculatorBo
 *
 * @author chenyifei
 * @since 2024/12/18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiquidityBoardCalculatorBo extends LiquidityBaseCalculatorBo {

    /**
     * 预测时间
     */
    private LocalDate queryDate;

    /**
     * 预测天数
     */
    private Integer predictDay;

    /**
     * 预测时间开始【监管户资金余额】>> 左边、【非监管户的资金余额】>> 右边 的结果
     * 计算净流入的时候需要使用
     */
    private Pair<LiquidityColorVo, LiquidityColorVo> superviseAndNonSuperviseBalance;

    private LocalDate queryDateStart;
}
