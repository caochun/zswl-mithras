package cn.zswltech.mithras.service.service.ftp.fms;

import cn.zswltech.mithras.service.service.projfms.ProjContext;
import cn.zswltech.mithras.service.service.projfms.ProjStateMachine;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/9/2 17:36
 */
public interface FtpAction {
    public abstract void execute(FtpStateMachine stateMachine, FtpContext context);
}
