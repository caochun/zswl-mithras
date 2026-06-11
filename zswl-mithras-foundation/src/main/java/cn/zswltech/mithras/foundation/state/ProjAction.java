package cn.zswltech.mithras.foundation.state;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/9/2 17:36
 */
public interface ProjAction{
    public abstract void execute(ProjStateMachine stateMachine, ProjContext context);
}
