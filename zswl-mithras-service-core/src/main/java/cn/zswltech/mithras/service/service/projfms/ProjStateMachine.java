package cn.zswltech.mithras.service.service.projfms;

import cn.zswltech.mithras.service.service.projfms.actions.ProcessStateChangeAction;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cn.zswltech.mithras.service.service.projfms.ProjEvent.*;
import static cn.zswltech.mithras.service.service.projfms.ProjProcessState.NEW_REJECT;
import static cn.zswltech.mithras.service.service.projfms.ProjProcessState.*;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/9/2 17:37
 */
public abstract class ProjStateMachine<T extends IStateMachineEntity> {
    private final List<ProjStateMachineTransition> transitions = new ArrayList<>();
    @Autowired
    private BaseMapper<T> baseMapper;

    private final ProcessStateChangeAction<T> stateChangeAction = new ProcessStateChangeAction<>();

    @PostConstruct
    public void init() {
        stateChangeAction.setBaseMapper(baseMapper);
    }

    protected List<ProjStateMachineTransition> getTransitions() {
        return transitions;
    }

    protected ProcessStateChangeAction<T> getStateChangeAction() {
        return stateChangeAction;
    }

    public ProjStateMachine() {
        // 新建未提交 -> 提交审批 -> 新建审批中
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(NEW_UN_SUBMIT)
                .event(SUBMIT_APPROVAL)
                .nextState(NEW_UNDER_APPROVAL)
                .action(stateChangeAction)
                .build());
        // 取消新建 -> 提交审批 -> 新建审批中
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(CANCEL_NEW)
                .event(SUBMIT_APPROVAL)
                .nextState(NEW_UNDER_APPROVAL)
                .action(stateChangeAction)
                .build());
        // 新建拒绝 -> 提交审批 -> 新建审批中
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(NEW_REJECT)
                .event(SUBMIT_APPROVAL)
                .nextState(NEW_UNDER_APPROVAL)
                .action(stateChangeAction)
                .build());
        // 新建审批中 -> 取消审批 -> 新建未提交
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(NEW_UNDER_APPROVAL)
                .event(NEW_WITHDRAW)
                .nextState(NEW_UN_SUBMIT)
                .action(stateChangeAction)
                .build());
        // 新建审批中 -> 关闭 -> 取消新建
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(NEW_UNDER_APPROVAL)
                .event(DISABLE)
                .nextState(CANCEL_NEW)
                .action(stateChangeAction)
                .build());
        // 新建审批中 -> 审批通过 -> 新建审批通过
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(NEW_UNDER_APPROVAL)
                .event(ProjEvent.APPROVAL_PASS)
                .nextState(NEW_APPROVAL_PASS)
                .action(stateChangeAction)
                .build());
        // 新建审批中 -> 审批拒绝 -> 新建审批拒绝
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(NEW_UNDER_APPROVAL)
                .event(ProjEvent.NEW_REJECT)
                .nextState(NEW_REJECT)
                .action(stateChangeAction)
                .build());
        // 新建审批拒绝 -> 提交审批 -> 新建审批中
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(NEW_REJECT)
                .event(SUBMIT_APPROVAL)
                .nextState(NEW_UNDER_APPROVAL)
                .action(stateChangeAction)
                .build());
        // 新建审批通过 -> 变更保存 -> 变更未提交
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(NEW_APPROVAL_PASS)
                .event(MODIFY_SAVE)
                .nextState(CHANGING_UN_SUBMIT)
                .action(stateChangeAction)
                .build());
        // 变更未提交 -> 提交审批 -> 变更审批中
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(CHANGING_UN_SUBMIT)
                .event(SUBMIT_APPROVAL)
                .nextState(CHANGING_UNDER_APPROVAL)
                .action(stateChangeAction)
                .build());
        // 变更审批中 -> 审批通过 -> 变更审批通过
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(CHANGING_UNDER_APPROVAL)
                .event(ProjEvent.APPROVAL_PASS)
                .nextState(CHANGING_APPROVAL_PASS)
                .action(stateChangeAction)
                .build());
        // 变更审批通过 -> 变更保存 -> 变更未提交
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(CHANGING_APPROVAL_PASS)
                .event(MODIFY_SAVE)
                .nextState(CHANGING_UN_SUBMIT)
                .action(stateChangeAction)
                .build());
        // 变更审批中 -> 审批取消 -> 取消变更
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(CHANGING_UNDER_APPROVAL)
                .event(MODIFY_WITHDRAW)
                .nextState(CANCEL_CHANGE)
                .action(stateChangeAction)
                .build());
        // 变更审批中 -> 关闭 -> 取消变更
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(CHANGING_UNDER_APPROVAL)
                .event(DISABLE)
                .nextState(CANCEL_CHANGE)
                .action(stateChangeAction)
                .build());
        // 变更审批中 -> 审批拒绝 -> 变更审批拒绝
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(CHANGING_UNDER_APPROVAL)
                .event(MODIFY_REJECT)
                .nextState(CHANGE_REJECT)
                .action(stateChangeAction)
                .build());
        // 变更审批拒绝 -> 提交审批 -> 变更审批中
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(CHANGE_REJECT)
                .event(SUBMIT_APPROVAL)
                .nextState(CHANGING_UNDER_APPROVAL)
                .action(stateChangeAction)
                .build());
        // 取消变更 -> 保存变更 -> 变更未提交
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(CANCEL_CHANGE)
                .event(MODIFY_SAVE)
                .nextState(CHANGING_UN_SUBMIT)
                .action(stateChangeAction)
                .build());
        // 取消变更 -> 提交审批 -> 变更审批中
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(CANCEL_CHANGE)
                .event(SUBMIT_APPROVAL)
                .nextState(CHANGING_UNDER_APPROVAL)
                .action(stateChangeAction)
                .build());
        // 变更审批拒绝 -> 保存变更 -> 变更未提交
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(CHANGE_REJECT)
                .event(MODIFY_SAVE)
                .nextState(CHANGING_UN_SUBMIT)
                .action(stateChangeAction)
                .build());

        // 未提交 -> 提交审批 -> 审批中
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(UN_SUBMIT)
                .event(SUBMIT_APPROVAL)
                .nextState(UNDER_APPROVAL)
                .action(stateChangeAction)
                .build());
        // 未提交 -> 取消操作 -> 已取消
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(UN_SUBMIT)
                .event(NEW_WITHDRAW)
                .nextState(CANCELED)
                .action(stateChangeAction)
                .build());
        // 已取消 -> 修改 -> 未提交
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(CANCELED)
                .event(MODIFY_SAVE)
                .nextState(UN_SUBMIT)
                .action(stateChangeAction)
                .build());
        // 审批中 -> 取消审批 -> 未提交
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(UNDER_APPROVAL)
                .event(NEW_WITHDRAW)
                .nextState(UN_SUBMIT)
                .action(stateChangeAction)
                .build());
        // 变更审批中 -> 审批拒绝 -> 变更审批拒绝
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(UNDER_APPROVAL)
                .event(ProjEvent.NEW_REJECT)
                .nextState(APPROVAL_REJECT)
                .action(stateChangeAction)
                .build());
        // 审批中 -> 关闭 -> 取消
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(UNDER_APPROVAL)
                .event(DISABLE)
                .nextState(CANCEL)
                .action(stateChangeAction)
                .build());
        // 审批中 -> 审批通过 -> 审批通过
        transitions.add(ProjStateMachineTransition.builder()
                .currentState(UNDER_APPROVAL)
                .event(ProjEvent.APPROVAL_PASS)
                .nextState(ProjProcessState.APPROVAL_PASS)
                .action(stateChangeAction)
                .build());
    }

    public void execute(ProjContext<T> context) {
        Optional<ProjStateMachineTransition> opt = transitions.stream()
                .filter(it -> it.getCurrentState().equals(context.getState()) && it.getEvent().equals(context.getEvent()))
                .findAny();
        if (!opt.isPresent()) {
            return;
        }
        ProjStateMachineTransition transition = opt.get();
        context.setState(transition.getNextState());
        transition.getAction().execute(this, context);
    }

}
