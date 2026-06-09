package cn.zswltech.mithras.service.service.projfms;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/9/2 17:37
 */
@Data
@AllArgsConstructor
public class ProjContext<T extends IStateMachineEntity> {

    private T t;
    /**
     * 调动时为当前状态，经过状态机后变成下级状态，由action执行更新
     */
    private ProcessStatus state;

    /**
     * 调用状态机时的事件
     */
    private ProjEvent event;

    public static <E extends IStateMachineEntity> ProjContext of(E t, ProjEvent event, ProcessStatus currentState){
        ProjContext projContext = new ProjContext(t, currentState, event);
        return projContext;
    }
}
