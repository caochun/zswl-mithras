package cn.zswltech.mithras.ftp.newftp.fms;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/9/2 17:36
 */
public interface NewFtpAction {
    public abstract void execute(NewFtpStateMachine stateMachine, NewFtpContext context);
}
