package cn.zswltech.mithras.service.job;

import cn.zswltech.mithras.service.CreditLimitService;
import cn.zswltech.mithras.service.enums.CreditLimitStatusEnum;
import cn.zswltech.mithras.service.mapper.model.CreditLimit;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundCredit;
import cn.zswltech.mithras.service.service.fund.FundCreditService;
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
    private FundCreditService fundCreditService;

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
        fundCreditService.update(Wrappers.<FundCredit>lambdaUpdate()
                .lt(FundCredit::getEffectiveDateTo, now)
                .eq(FundCredit::getEffective, Boolean.TRUE)
                .set(FundCredit::getEffective, Boolean.FALSE));
    }
}
