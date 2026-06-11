package cn.zswltech.mithras.ftp.oldftp.fms;

import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.foundation.state.IStateMachineEntity;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpProcessStatus;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.Data;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description: 新建未提交 ——> 新建审批中
 * @author: zhaozhengkang
 * @date: 2022/9/2 17:39
 */
@Data
public class FtpProcessStateChangeAction<T extends IStateMachineEntity> implements FtpAction {

    private BaseMapper<T> baseMapper;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void execute(FtpStateMachine stateMachine, FtpContext context) {
        IStateMachineEntity t = context.getT();
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("id", t.getId());
        updateWrapper.set(t.getProcessStatusFieldName(), context.getState().name());
        if (context.getState().equals(FtpProcessStatus.NEW_APPROVAL_PASS) && context.getEvent().equals(FtpEvent.APPROVAL_PASS)) {
            updateWrapper.set(t.getRecordStatusFieldName(), RecordStatus.TAKE_EFFECT.name());
        }
        baseMapper.update(null, updateWrapper);
    }
}
