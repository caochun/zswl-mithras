package cn.zswltech.mithras.service.service.newftp.fms;

import cn.zswltech.mithras.service.service.projfms.ProcessStatus;
import lombok.Builder;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/9/2 17:37
 */
@Data
@Builder
public class NewFtpStateMachineTransition {
    /**
     * 当前状态
     */
    ProcessStatus currentState;

    /**
     * 接收到事件后做的操作
     */
    NewFtpAction action;

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
    NewFtpEvent event;
}
