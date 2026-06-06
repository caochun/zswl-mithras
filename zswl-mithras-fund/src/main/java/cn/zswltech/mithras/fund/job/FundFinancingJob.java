package cn.zswltech.mithras.fund.job;

import cn.zswltech.mithras.fund.application.financing.FundFinancingPlanRateAdjustService;
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
    private FundFinancingPlanRateAdjustService fundFinancingPlanRateAdjustService;


    @XxlJob("fundFinancingAutoAdjustRateJob")
    @Transactional(rollbackFor = Exception.class)
    public void autoAdjustRate() {
        try {
            String param = XxlJobHelper.getJobParam();
            fundFinancingPlanRateAdjustService.autoAdjustRate(param);
        } catch (Exception e){
            log.error("fundFinancingAutoAdjustRateJob has error", e);
        }
    }


}
