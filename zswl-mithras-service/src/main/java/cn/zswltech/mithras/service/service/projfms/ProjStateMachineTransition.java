package cn.zswltech.mithras.service.service.projfms;

import lombok.Builder;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/9/2 17:37
 */
@Data
@Builder
public class ProjStateMachineTransition {
    /**
     * 当前状态
     */
    ProjProcessState currentState;

    /**
     * 接收到事件后做的操作
     */
    ProjAction action;

    /**
     * 节点名称
     */
    String actionName;

    /**
     * 下一个状态
     */
    ProjProcessState nextState;

    /**
     * 接受的事件
     */
    ProjEvent event;
}
