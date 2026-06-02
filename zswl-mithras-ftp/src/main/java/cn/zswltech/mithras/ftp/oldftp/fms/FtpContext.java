package cn.zswltech.mithras.ftp.oldftp.fms;

import cn.zswltech.mithras.service.service.projfms.IStateMachineEntity;
import cn.zswltech.mithras.service.service.projfms.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/9/2 17:37
 */
@Data
@AllArgsConstructor
public class FtpContext<T extends IStateMachineEntity> {

    private T t;
    /**
     * 调动时为当前状态，经过状态机后变成下级状态，由action执行更新
     */
    private ProcessStatus state;

    /**
     * 调用状态机时的事件
     */
    private FtpEvent event;

    public static <E extends IStateMachineEntity> FtpContext of(E t, FtpEvent event, ProcessStatus currentState){
        FtpContext context = new FtpContext(t, currentState, event);
        return context;
    }
}
