package cn.zswltech.mithras.fund.application.financing.statemachine;

import cn.zswltech.mithras.fund.enums.financing.FundFinancingEvent;
import cn.zswltech.mithras.foundation.state.ProcessStatus;
import lombok.Builder;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/9/2 17:37
 */
@Data
@Builder
public class FundFinancingStateMachineTransition {
    /**
     * 当前状态
     */
    ProcessStatus currentState;

    /**
     * 接收到事件后做的操作
     */
    FundFinancingAction action;

    /**
     * 节点名称
     */
    String actionName;

    /**
     * 下一个状态
     */
    ProcessStatus nextState;

    /**
     * 接受的事件
     */
    FundFinancingEvent event;
}
