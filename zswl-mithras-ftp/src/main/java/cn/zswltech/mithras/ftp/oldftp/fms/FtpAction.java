package cn.zswltech.mithras.ftp.oldftp.fms;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/9/2 17:36
 */
public interface FtpAction {
    public abstract void execute(FtpStateMachine stateMachine, FtpContext context);
}
