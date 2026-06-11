package cn.zswltech.mithras.payment.application.checker;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.CurrentUserJobResolver;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailUnconfirmedMapper;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetailUnconfirmed;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.flow.service.ProcessService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/12/14
 * @description
 */
@Component
public class PaymentActualDetailRemoveAuthChecker implements IDataAuthChecker {
    @Resource
    private CurrentUserJobResolver currentUserJobResolver;
    @Resource
    private ProcessService processService;
    @Resource
    private PaymentActualDetailUnconfirmedMapper paymentActualDetailUnconfirmedMapper;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        boolean isMoneyManager = currentUserJobResolver.userIsSpecificJob(currentUserId, JobEnum.moneymanager.name(), JobEnum.deepmoneymanager.name());
        if (!isMoneyManager) {
            throw new AuthCheckException("仅资金经理可以操作");
        }
        PaymentActualDetailUnconfirmed paymentActualDetailUnconfirmed = paymentActualDetailUnconfirmedMapper.selectById(keyId);
        if (Objects.isNull(paymentActualDetailUnconfirmed)) {
            throw new MithrasException("未确认的付款核销记录不存在");
        }
        boolean isInProcess = processService.isInProcess(String.valueOf(keyId), Collections.singletonList(ProcessModelTypeEnum.PaymentActualDetailFlow.name()));
        if (isInProcess) {
            throw new MithrasException("审批流程中，不允许删除");
        }
        return true;
    }
}
