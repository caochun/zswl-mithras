package cn.zswltech.mithras.creditlimit.job;

import cn.zswltech.mithras.creditlimit.service.CreditLimitService;
import cn.zswltech.mithras.creditlimit.enums.CreditLimitStatusEnum;
import cn.zswltech.mithras.creditlimit.mapper.model.CreditLimit;
import cn.zswltech.mithras.creditlimit.service.port.FundCreditEffectiveStatusService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/10/14
 * @description
 */
@Component
public class CreditLimitJob {
    @Resource
    private CreditLimitService creditLimitService;
    @Resource
    private FundCreditEffectiveStatusService fundCreditEffectiveStatusService;

    @XxlJob("creditLimitStatusDailyJob")
    public void creditLimitStatusDailyJob() {
        LocalDate now = LocalDate.now();
        // 更新生效
        LambdaUpdateWrapper<CreditLimit> updateEffectWrapper = Wrappers.lambdaUpdate();
        updateEffectWrapper.set(CreditLimit::getStatus, CreditLimitStatusEnum.EFFECTIVE.name());
        updateEffectWrapper.le(CreditLimit::getEffectiveDateFrom, now);
        updateEffectWrapper.ge(CreditLimit::getEffectiveDateTo, now);
        updateEffectWrapper.ne(CreditLimit::getStatus, CreditLimitStatusEnum.EFFECTIVE.name());
        creditLimitService.update(updateEffectWrapper);
        // 更新失效
        LambdaUpdateWrapper<CreditLimit> updateInvalidWrapper = Wrappers.lambdaUpdate();
        updateInvalidWrapper.set(CreditLimit::getStatus, CreditLimitStatusEnum.INVALID.name());
        updateInvalidWrapper.lt(CreditLimit::getEffectiveDateTo, now);
        updateInvalidWrapper.ne(CreditLimit::getStatus, CreditLimitStatusEnum.INVALID.name());
        creditLimitService.update(updateInvalidWrapper);

        // 同步处理授信业务表
        fundCreditEffectiveStatusService.invalidExpiredFundCredit(now);
    }
}
