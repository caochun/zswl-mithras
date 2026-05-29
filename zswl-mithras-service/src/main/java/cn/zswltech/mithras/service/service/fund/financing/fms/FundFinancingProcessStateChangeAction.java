package cn.zswltech.mithras.service.service.fund.financing.fms;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.common.enums.RecordStatus;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingProcessStatus;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingStatusEnum;
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
public class FundFinancingProcessStateChangeAction<T extends IFundFinancingStateMachineEntity> implements FundFinancingAction {

    private BaseMapper<T> baseMapper;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void execute(FundFinancingStateMachine stateMachine, FundFinancingContext context, String secondStatus) {
        IFundFinancingStateMachineEntity t = context.getData();
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("id", t.getId());
        updateWrapper.set(t.getProcessStatusFieldName(), context.getState());
        updateWrapper.set(t.getSecondRecordStatusFieldName(), secondStatus);
        if (context.getState().equals(FundFinancingProcessStatus.NEW_APPROVAL_PASS) && context.getEvent().equals(FundFinancingEvent.APPROVAL_PASS)) {
            updateWrapper.set(t.getRecordStatusFieldName(), FundFinancingStatusEnum.EFFECT.name());
        }
        baseMapper.update(null, updateWrapper);
    }
}
