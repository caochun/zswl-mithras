package cn.zswltech.mithras.service.service.ftp.fms;

import cn.zswltech.mithras.service.service.projfms.ProcessStatus;
import cn.zswltech.mithras.service.service.projfms.ProjAction;
import cn.zswltech.mithras.service.service.projfms.ProjEvent;
import cn.zswltech.mithras.service.service.projfms.ProjProcessState;
import lombok.Builder;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/9/2 17:37
 */
@Data
@Builder
public class FtpStateMachineTransition {
    /**
     * 当前状态
     */
    ProcessStatus currentState;

    /**
     * 接收到事件后做的操作
     */
    FtpAction action;

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
    FtpEvent event;
}
