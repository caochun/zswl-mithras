package cn.zswltech.mithras.projectprocess.application.statemachine;

import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.foundation.state.ProjEvent;
import cn.zswltech.mithras.foundation.state.ProjProcessState;
import cn.zswltech.mithras.foundation.state.ProjStateMachine;
import cn.zswltech.mithras.foundation.state.ProjStateMachineTransition;
import org.springframework.stereotype.Service;


/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/9/6 10:29
 */
@Service
public class ProjEstablishStateMachine extends ProjStateMachine<ProjEstablishBaseInfo> {
    public ProjEstablishStateMachine() {
        super();
        // 新建审批中 -> 审批拒绝 -> 新建审批拒绝
        this.getTransitions().add(ProjStateMachineTransition.builder()
                .currentState(ProjProcessState.NEW_UNDER_APPROVAL)
                .event(ProjEvent.NEW_REJECT)
                .nextState(ProjProcessState.NEW_REJECT)
                .action(this.getStateChangeAction())
                .build());
        // 变更审批中 -> 审批拒绝 -> 变更审批拒绝
        this.getTransitions().add(ProjStateMachineTransition.builder()
                .currentState(ProjProcessState.CHANGING_UNDER_APPROVAL)
                .event(ProjEvent.MODIFY_REJECT)
                .nextState(ProjProcessState.CHANGE_REJECT)
                .action(this.getStateChangeAction())
                .build());
    }
}
