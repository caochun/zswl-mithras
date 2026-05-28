package cn.zswltech.mithras.service.job;

import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPlanService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @ClassName FundFinancingJob
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/6/28 10:24 上午
 * @Version 1.0
 **/
@Component
@Slf4j
public class FundFinancingJob {

    @Resource
    private FundFinancingPlanService fundFinancingPlanService;


    @XxlJob("fundFinancingAutoAdjustRateJob")
    @Transactional(rollbackFor = Exception.class)
    public void autoAdjustRate() {
        try {
            String param = XxlJobHelper.getJobParam();
            fundFinancingPlanService.autoAdjustRate(param);
        } catch (Exception e){
            log.error("fundFinancingAutoAdjustRateJob has error", e);
        }
    }


}
