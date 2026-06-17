package cn.zswltech.mithras.finance.monthly.job;

import cn.zswltech.mithras.finance.monthly.application.port.FundsDailyCostJobPort;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2025/11/5
 * @description
 */
@Slf4j
@Component
public class FundsDailyCostJob {

    @Resource
    private FundsDailyCostJobPort fundsDailyCostJobService;

    @XxlJob("fundsDailyCostMainFinishJob")
    public void fundsDailyCostMainFinishJob() {
        fundsDailyCostJobService.fundsDailyCostMainFinishJob(XxlJobHelper.getJobParam());
    }

    @XxlJob("fundsDailyCostInit")
    public void fundsDailyCostInit() {
        fundsDailyCostJobService.fundsDailyCostInit(XxlJobHelper.getJobParam());
    }
}
