package cn.zswltech.mithras.service.auth.checker.payment;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.rule.DataAuthProcessRule;
import cn.zswltech.mithras.service.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.workflow.application.flow.service.ProcessService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
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
    private SysUserService sysUserService;
    @Resource
    private DataAuthProcessRule dataAuthProcessRule;
    @Resource
    private ProcessService processService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        boolean isMoneyManager = sysUserService.userIsSpecificJob(currentUserId, JobEnum.moneymanager.name(), JobEnum.deepmoneymanager.name());
        if (!isMoneyManager) {
            throw new AuthCheckException("仅资金经理可以操作");
        }
        dataAuthProcessRule.check(businessModule, keyId);
        // 额外判断是否正在进行对应付款申请结束投放操作
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(keyId);
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
