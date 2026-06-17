package cn.zswltech.mithras.ftp.oldftp.job;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.ftp.oldftp.application.port.FtpIncomeJobPort;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

@Slf4j
@Component
public class FtpIncomeJob {

    @Resource
    private FtpIncomeJobPort ftpIncomeJobService;

    @XxlJob("ftpIncomeMaintenanceJob")
    public void ftpIncomeMaintenanceJob() {
        try {
            ftpIncomeJobService.ftpIncomeMaintenance(resolveTargetDate());
        } catch (Exception e) {
            log.error("ftpIncomeMaintenanceJob has error", e);
        }
    }

    private LocalDate resolveTargetDate() {
        String jobParam = XxlJobHelper.getJobParam();
        if (StrUtil.isNotBlank(jobParam)) {
            return LocalDateTimeUtil.parse(jobParam, DatePattern.NORM_DATE_PATTERN).toLocalDate();
        }
        return LocalDate.now().minusDays(1);
    }
}
