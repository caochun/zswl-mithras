package cn.zswltech.mithras.ftp.newftp.fms;

import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.ftp.newftp.enums.NewFtpProcessStatus;
import cn.zswltech.mithras.service.service.projfms.IStateMachineEntity;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.Data;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @description: 新建未提交 ——> 新建审批中
 * @author: zhaozhengkang
 * @date: 2022/9/2 17:39
 */
@Data
public class NewFtpProcessStateChangeAction<T extends IStateMachineEntity> implements NewFtpAction {

    private BaseMapper<T> baseMapper;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void execute(NewFtpStateMachine stateMachine, NewFtpContext context) {
        IStateMachineEntity t = context.getT();
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("id", t.getId());
        updateWrapper.set(t.getProcessStatusFieldName(), context.getState().name());
        if (context.getState().equals(NewFtpProcessStatus.NEW_APPROVAL_PASS) &&
                context.getEvent().equals(NewFtpEvent.APPROVAL_PASS)) {
            updateWrapper.set(t.getRecordStatusFieldName(), RecordStatus.TAKE_EFFECT.name());
            updateWrapper.set("effect_time", LocalDateTime.now());
        }
        baseMapper.update(null, updateWrapper);
    }
}
