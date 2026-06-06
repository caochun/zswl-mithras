package cn.zswltech.mithras.riskcontrol.job;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.riskcontrol.application.job.RiskControlJobService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

@Component
@Slf4j
public class RiskControlJob {

    @Resource
    private RiskControlJobService riskControlJobService;

    @XxlJob("syncRiskControlHandler")
    public void demoJobHandler() {
        riskControlJobService.syncRiskControl();
    }

    @XxlJob("startWarnFlowJob")
    public void startWarnFlowJob() {
        riskControlJobService.startWarnFlow(resolveTargetDate());
    }

    @XxlJob("riskControlPaymentFlowJob")
    public void riskControlPaymentFlowJob() {
        riskControlJobService.riskControlPaymentFlow(XxlJobHelper.getJobParam());
    }

    private LocalDate resolveTargetDate() {
        String param = XxlJobHelper.getJobParam();
        if (ObjectUtil.isEmpty(param)) {
            return LocalDate.now();
        }
        return LocalDateTimeUtil.parse(param, "yyyy-MM-dd").toLocalDate();
    }
}
