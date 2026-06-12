package cn.zswltech.mithras.fund.application.financing.statemachine;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/9/2 17:36
 */
public interface FundFinancingAction {
    public abstract void execute(FundFinancingStateMachine stateMachine, FundFinancingContext context, String secondStatus);
}
