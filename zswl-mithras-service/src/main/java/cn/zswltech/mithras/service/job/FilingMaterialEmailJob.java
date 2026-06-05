package cn.zswltech.mithras.service.job;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.filingmaterials.domain.enums.FilingMaterialsFilingTypeEnum;
import cn.zswltech.mithras.filingmaterials.domain.enums.FilingMaterialsInitiationMethodEnum;
import cn.zswltech.mithras.service.service.filingmaterials.FilingMaterialsService;
import cn.zswltech.mithras.basedata.util.DateUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author linlili
 */
@Component
@Slf4j
public class FilingMaterialEmailJob {

    @Autowired
    private FilingMaterialsService filingMaterialsService;

    /**
     * 项目资料归档邮件发送
     */
    @XxlJob("filingMaterialEmailJob")
    public void filingMaterialEmailJob() {
        try {
            log.info("filingMaterialEmailJob 开始扫描");
            LocalDateTime date = LocalDateTimeUtil.beginOfDay(LocalDateTime.now());
            // 如果当天是非工作日则不执行任务
            if (!DateUtil.isWorkday(date.toLocalDate())) {
                log.info("今日非工作日，不执行项目资料归档逾期/催收邮件发送逻辑");
                return;
            }
            log.info("filingMaterialEmailJob start");
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            String jobParam = XxlJobHelper.getJobParam();
            filingMaterialsService.sendEmailFilingMaterials(jobParam);
            stopWatch.stop();
            log.info("filingMaterialEmailJob end!!! 耗时={}s", stopWatch.prettyPrint(TimeUnit.SECONDS));
        } catch (Exception e) {
            log.error("项目资料归档邮件发送任务失败:{}",e.getMessage());
        }
    }

    /**
     * 项目资料归档手动发起流程
     */
    @XxlJob("projectFilingMaterialInitJob")
    public void projectFilingMaterialInitJob() {
        try {
            String jobParam = XxlJobHelper.getJobParam();
            if (CharSequenceUtil.isEmpty(jobParam)) {
                return;
            }
            List<String> contractIdList = new ArrayList<>(Arrays.asList(jobParam.split(",")));
            for (String contractId : contractIdList) {
                filingMaterialsService.initCommonProcessPrepare(Long.valueOf(contractId), FilingMaterialsFilingTypeEnum.BUSINESS_MATERIALS.name(),
                        FilingMaterialsInitiationMethodEnum.SYSTEM.name());
            }
        } catch (Exception e) {
            log.error("项目资料归档流程发起失败:{}",e.getMessage());
        }
    }
}