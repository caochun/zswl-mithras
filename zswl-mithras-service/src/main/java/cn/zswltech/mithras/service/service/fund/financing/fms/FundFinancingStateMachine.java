package cn.zswltech.mithras.service.service.fund.financing.fms;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cn.zswltech.mithras.service.enums.fund.financing.FundFinancingProcessStatus.*;
import static cn.zswltech.mithras.service.service.fund.financing.fms.FundFinancingEvent.*;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/9/2 17:37
 */
public abstract class FundFinancingStateMachine<T extends IFundFinancingStateMachineEntity> {
    private final List<FundFinancingStateMachineTransition> transitions = new ArrayList<>();
    @Autowired
    private BaseMapper<T> baseMapper;

    private final FundFinancingProcessStateChangeAction<T> stateChangeAction = new FundFinancingProcessStateChangeAction();

    @PostConstruct
    public void init() {
        stateChangeAction.setBaseMapper(baseMapper);
    }

    public FundFinancingStateMachine() {
        // 新建未提交 -> 提交审批 -> 新建审批中
        transitions.add(FundFinancingStateMachineTransition.builder()
                .currentState(NEW_UN_SUBMIT)
                .event(SUBMIT_APPROVAL)
                .nextState(NEW_UNDER_APPROVAL)
                .action(stateChangeAction)
                .build());
        // 取消新建 -> 提交审批 -> 新建审批中
        transitions.add(FundFinancingStateMachineTransition.builder()
                .currentState(CANCEL_NEW)
                .event(SUBMIT_APPROVAL)
                .nextState(NEW_UNDER_APPROVAL)
                .action(stateChangeAction)
                .build());
        // 新建审批中 -> 取消审批 -> 新建未提交
        transitions.add(FundFinancingStateMachineTransition.builder()
                .currentState(NEW_UNDER_APPROVAL)
                .event(NEW_WITHDRAW)
                .nextState(CANCEL_NEW)
                .action(stateChangeAction)
                .build());
        // 新建审批中 -> 审批通过 -> 新建审批通过
        transitions.add(FundFinancingStateMachineTransition.builder()
                .currentState(NEW_UNDER_APPROVAL)
                .event(APPROVAL_PASS)
                .nextState(NEW_APPROVAL_PASS)
                .action(stateChangeAction)
                .build());
        // 新建审批中 -> 审批拒绝 -> 新建审批拒绝
        transitions.add(FundFinancingStateMachineTransition.builder()
                .currentState(NEW_UNDER_APPROVAL)
                .event(APPROVAL_REJECT)
                .nextState(NEW_APPROVAL_REJECT)
                .action(stateChangeAction)
                .build());
        // 新建审批通过 -> 变更保存 -> 变更未提交
        transitions.add(FundFinancingStateMachineTransition.builder()
                .currentState(NEW_APPROVAL_PASS)
                .event(MODIFY_SAVE)
                .nextState(CHANGING_UN_SUBMIT)
                .action(stateChangeAction)
                .build());
        // 变更生效 -> 变更保存 -> 变更未提交
        transitions.add(FundFinancingStateMachineTransition.builder()
                .currentState(CHANGING_EFFECT)
                .event(MODIFY_SAVE)
                .nextState(CHANGING_UN_SUBMIT)
                .action(stateChangeAction)
                .build());
        // 变更未提交 -> 确认 -> 变更生效
        transitions.add(FundFinancingStateMachineTransition.builder()
                .currentState(CHANGING_UN_SUBMIT)
                .event(CONFIRM)
                .nextState(CHANGING_EFFECT)
                .action(stateChangeAction)
                .build());
        // 变更未提交 -> 提交审批 -> 变更审批中
        transitions.add(FundFinancingStateMachineTransition.builder()
                .currentState(CHANGING_UN_SUBMIT)
                .event(SUBMIT_APPROVAL)
                .nextState(CHANGING_UNDER_APPROVAL)
                .action(stateChangeAction)
                .build());
        // 变更审批中 -> 审批通过 -> 变更审批通过
        transitions.add(FundFinancingStateMachineTransition.builder()
                .currentState(CHANGING_UNDER_APPROVAL)
                .event(APPROVAL_PASS)
                .nextState(CHANGING_APPROVAL_PASS)
                .action(stateChangeAction)
                .build());
        // 变更审批通过 -> 变更保存 -> 变更未提交
        transitions.add(FundFinancingStateMachineTransition.builder()
                .currentState(CHANGING_APPROVAL_PASS)
                .event(MODIFY_SAVE)
                .nextState(CHANGING_UN_SUBMIT)
                .action(stateChangeAction)
                .build());
        // 变更审批中 -> 审批取消 -> 取消变更
        transitions.add(FundFinancingStateMachineTransition.builder()
                .currentState(CHANGING_UNDER_APPROVAL)
                .event(MODIFY_WITHDRAW)
                .nextState(CANCEL_CHANGE)
                .action(stateChangeAction)
                .build());
        // 取消变更 -> 保存变更 -> 变更未提交
        transitions.add(FundFinancingStateMachineTransition.builder()
                .currentState(CANCEL_CHANGE)
                .event(MODIFY_SAVE)
                .nextState(CHANGING_UN_SUBMIT)
                .action(stateChangeAction)
                .build());
        // 取消变更 -> 提交审批 -> 变更审批中
        transitions.add(FundFinancingStateMachineTransition.builder()
                .currentState(CANCEL_CHANGE)
                .event(SUBMIT_APPROVAL)
                .nextState(CHANGING_UNDER_APPROVAL)
                .action(stateChangeAction)
                .build());
        // 变更审批拒绝 -> 保存变更 -> 变更未提交
        transitions.add(FundFinancingStateMachineTransition.builder()
                .currentState(CHANGING_APPROVAL_REJECT)
                .event(MODIFY_SAVE)
                .nextState(CHANGING_UN_SUBMIT)
                .action(stateChangeAction)
                .build());
    }

    public void execute(FundFinancingContext<T> context, String secondStatus) {
        Optional<FundFinancingStateMachineTransition> opt = transitions.stream()
                .filter(it -> it.getCurrentState().equals(context.getState()) && it.getEvent().equals(context.getEvent()))
                .findAny();
        if (!opt.isPresent()) {
            return;
        }
        FundFinancingStateMachineTransition transition = opt.get();
        context.setState(transition.getNextState());
        transition.getAction().execute(this, context, secondStatus);
    }

}
