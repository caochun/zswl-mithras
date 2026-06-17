package cn.zswltech.mithras.ftp.oldftp.job;

import cn.zswltech.mithras.ftp.oldftp.application.port.FtpIncomeRateInitJobPort;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author bigbear
 * @version 1.0
 * @description FTP收益率初始化任务
 * @since 2025/8/21 16:08
 **/
@Slf4j
@Component
public class FtpIncomeRateInitJob {

    @Resource
    private FtpIncomeRateInitJobPort ftpIncomeRateInitJobService;

    @XxlJob(value = "ftpIncomeRateInitJob")
    public void ftpIncomeRateInitJob() {
        ftpIncomeRateInitJobService.ftpIncomeRateInitJob();
    }
}
