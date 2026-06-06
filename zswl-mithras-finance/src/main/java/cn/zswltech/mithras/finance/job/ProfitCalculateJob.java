package cn.zswltech.mithras.finance.job;

import cn.zswltech.mithras.finance.application.job.ProfitCalculateJobService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class ProfitCalculateJob {

    @Resource
    private ProfitCalculateJobService profitCalculateJobService;

    @XxlJob("profitCalculate")
    public void profitCalculate() {
        profitCalculateJobService.profitCalculate(XxlJobHelper.getJobParam());
    }
}
