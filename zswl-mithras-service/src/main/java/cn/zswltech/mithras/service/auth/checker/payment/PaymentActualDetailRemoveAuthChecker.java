package cn.zswltech.mithras.service.auth.checker.payment;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetailUnconfirmed;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentActualDetailUnconfirmedMapper;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.flow.ProcessService;
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
    private SysUserService sysUserService;
    @Resource
    private ProcessService processService;
    @Resource
    private PaymentActualDetailUnconfirmedMapper paymentActualDetailUnconfirmedMapper;

    @Override
    public boolean check(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        boolean isMoneyManager = sysUserService.userIsSpecificJob(currentUserId, JobEnum.moneymanager.name(), JobEnum.deepmoneymanager.name());
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
