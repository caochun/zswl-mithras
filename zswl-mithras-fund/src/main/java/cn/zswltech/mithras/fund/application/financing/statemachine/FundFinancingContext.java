package cn.zswltech.mithras.fund.application.financing.statemachine;

import cn.zswltech.mithras.fund.enums.financing.FundFinancingEvent;
import cn.zswltech.mithras.foundation.state.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/9/2 17:37
 */
@Data
@AllArgsConstructor
public class FundFinancingContext<T extends IFundFinancingStateMachineEntity> {

    private T data;
    /**
     * 调动时为当前状态，经过状态机后变成下级状态，由action执行更新
     */
    private ProcessStatus state;

    /**
     * 调用状态机时的事件
     */
    private FundFinancingEvent event;

    public static <E extends IFundFinancingStateMachineEntity> FundFinancingContext<E> of(E t, FundFinancingEvent event, ProcessStatus currentState){
        return new FundFinancingContext<>(t, currentState, event);
    }
}
