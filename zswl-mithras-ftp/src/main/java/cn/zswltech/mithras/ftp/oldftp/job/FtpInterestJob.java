package cn.zswltech.mithras.ftp.oldftp.job;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.ftp.oldftp.application.port.FtpInterestJobPort;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

@Slf4j
@Component
public class FtpInterestJob {

    @Resource
    private FtpInterestJobPort ftpInterestJobService;

    @XxlJob("calculateFtpInterest")
    public void calculateFtpInterest() {
        try {
            String param = XxlJobHelper.getJobParam();
            FtpInterestJobParam jobParam = resolveJobParam(param);
            ftpInterestJobService.calculateFtpInterest(jobParam.getFtpInterestId(), jobParam.getInterestStartDate(), jobParam.getInterestEndDate());
        } catch (Exception e) {
            log.error("FTP计息任务执行异常", e);
        }
    }

    private FtpInterestJobParam resolveJobParam(String param) {
        if (StrUtil.isBlank(param)) {
            LocalDate now = LocalDate.now();
            return new FtpInterestJobParam(null, now, now);
        }
        log.info("FTP计息任务 - 控制台参数: {}", param);
        JSONObject jsonObject = JSONUtil.parseObj(param);
        Long targetFtpInterestId = jsonObject.getLong("ftpInterestId");
        String interestStartDateStr = jsonObject.getStr("interestStartDate");
        String interestEndDateStr = jsonObject.getStr("interestEndDate");
        if (StrUtil.isBlank(interestStartDateStr) || StrUtil.isBlank(interestEndDateStr)) {
            log.error("FTP计息任务-手动调用控制台参数不符合要求[{}]", param);
            throw new MithrasException("FTP计息任务-手动调用控制台参数不符合要求");
        }
        LocalDate interestStartDate = LocalDateTimeUtil.parseDate(interestStartDateStr, DatePattern.NORM_DATE_PATTERN);
        LocalDate interestEndDate = LocalDateTimeUtil.parseDate(interestEndDateStr, DatePattern.NORM_DATE_PATTERN);
        return new FtpInterestJobParam(targetFtpInterestId, interestStartDate, interestEndDate);
    }

    private static class FtpInterestJobParam {

        private final Long ftpInterestId;
        private final LocalDate interestStartDate;
        private final LocalDate interestEndDate;

        private FtpInterestJobParam(Long ftpInterestId, LocalDate interestStartDate, LocalDate interestEndDate) {
            this.ftpInterestId = ftpInterestId;
            this.interestStartDate = interestStartDate;
            this.interestEndDate = interestEndDate;
        }

        private Long getFtpInterestId() {
            return ftpInterestId;
        }

        private LocalDate getInterestStartDate() {
            return interestStartDate;
        }

        private LocalDate getInterestEndDate() {
            return interestEndDate;
        }
    }
}
