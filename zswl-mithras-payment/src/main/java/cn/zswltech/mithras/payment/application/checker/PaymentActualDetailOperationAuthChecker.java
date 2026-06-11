package cn.zswltech.mithras.payment.application.checker;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.DataAuthProcessGuard;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.CurrentUserJobResolver;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.flow.service.ProcessService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/12/14
 * @description
 */
@Component
public class PaymentActualDetailOperationAuthChecker implements IDataAuthChecker {
    @Resource
    private CurrentUserJobResolver currentUserJobResolver;
    @Resource
    private DataAuthProcessGuard dataAuthProcessGuard;
    @Resource
    private ProcessService processService;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        boolean isMoneyManager = currentUserJobResolver.userIsSpecificJob(currentUserId, JobEnum.moneymanager.name(), JobEnum.deepmoneymanager.name());
        if (!isMoneyManager) {
            throw new AuthCheckException("仅资金经理可以操作");
        }
        dataAuthProcessGuard.check(businessModule, keyId);
        // 额外判断是否正在进行对应付款申请结束投放操作
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(keyId);
        if (Objects.isNull(paymentBaseInfo)) {
            throw new MithrasException("付款申请不存在");
        }
        boolean isInProcess = processService.isInProcess(String.valueOf(paymentBaseInfo.getContractId()), Arrays.asList(ProcessModelTypeEnum.ContractStartRentAutoFlow.name(), ProcessModelTypeEnum.ContractAddNewReceiptAutoFlow.name()));
        if (isInProcess) {
            throw new MithrasException("对应合同正处于流程中，不允许操作");
        }
        return true;
    }
}
