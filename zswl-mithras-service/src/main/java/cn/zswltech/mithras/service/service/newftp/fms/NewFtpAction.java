package cn.zswltech.mithras.service.service.newftp.fms;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/9/2 17:36
 */
public interface NewFtpAction {
    public abstract void execute(NewFtpStateMachine stateMachine, NewFtpContext context);
}
