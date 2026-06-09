package cn.zswltech.mithras.riskcontrol.application;

import cn.zswltech.mithras.riskcontrol.common.AlertState;

import javax.annotation.Nullable;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/15 15:02
 */
public interface MetricCompartor {
    /**
     * 行业分类指标比较当前值和预警值
     *
     * @param thisAmount 若不为null，thisAmount金额需要add进currentValue参与比较
     * @return
     */
    boolean industryClassifyCanPass(@Nullable Long thisAmount);


    AlertState currentAlertState(Long currentValueOne, @Nullable Long currentValueTwo);
}
