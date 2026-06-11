package cn.zswltech.mithras.foundation.state.actions;

import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.foundation.state.*;
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
public class ProcessStateChangeAction<T extends IStateMachineEntity> implements ProjAction {

    private BaseMapper<T> baseMapper;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void execute(ProjStateMachine stateMachine, ProjContext context) {
        IStateMachineEntity t = context.getT();
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("id", t.getId());
        updateWrapper.set(t.getProcessStatusFieldName(), context.getState().name());
        if (context.getState().equals(ProjProcessState.NEW_APPROVAL_PASS) && context.getEvent().equals(ProjEvent.APPROVAL_PASS)) {
            updateWrapper.set(t.getRecordStatusFieldName(), RecordStatus.TAKE_EFFECT.name());
        } else if (context.getState().equals(ProjProcessState.APPROVAL_PASS) && context.getEvent().equals(ProjEvent.APPROVAL_PASS)) {
            updateWrapper.set(t.getRecordStatusFieldName(), RecordStatus.TAKE_EFFECT.name());
        }
        baseMapper.update(null, updateWrapper);
    }
}
