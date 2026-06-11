package cn.zswltech.mithras.report.job;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.report.handler.CrFacade;
import cn.zswltech.mithras.report.mapper.draft.model.CrRepayPlanDraft;
import cn.zswltech.mithras.report.service.draft.CrRepayPlanDraftService;
import cn.zswltech.mithras.foundation.cache.RedisDistLock;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static cn.zswltech.mithras.foundation.constant.ResultMsg.CONCURRENT_OPERATION;

/**
 * 征信报送定时任务
 *
 * @author wangchuanhao
 * @date 2022/10/9 2:58 PM
 */
@Slf4j
@Component
public class ReportJob {
    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Resource
    private CrFacade crFacade;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private CrRepayPlanDraftService crRepayPlanDraftService;

    @XxlJob("reportJobHandler")
    public void reportJobHandler() {
        LocalDateTime dealTime = LocalDateTime.now();
        log.info("征信报送定时任务开始, time:{}", LocalDateTimeUtil.format(dealTime, FORMAT));
        // 设置一个参数，时间可以从xxl-job传参进来
        String jobParam = XxlJobHelper.getJobParam();
        if (CharSequenceUtil.isNotBlank(jobParam)) {
            dealTime = LocalDateTimeUtil.parse(jobParam, DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN));
        }
        try {
            crFacade.handle(dealTime);
        } catch (Exception e) {
            log.error("报送错误", e);
        }
        log.info("征信报送定时任务结束, time:{}", LocalDateTimeUtil.format(LocalDateTime.now(), FORMAT));
    }


    /**
     * 暂时不使用，客户需求已经变更
     */
    @XxlJob("modifyGracePeriodJobHandler")
    public void modifyGracePeriodJobHandler() {
        LocalDateTime dealTime = LocalDateTime.now();
        log.info("征信修改到应还日期期项计划展示标志, time:{}", LocalDateTimeUtil.format(dealTime, FORMAT));
        String lock = "modifyGracePeriodLock";
        try {
            boolean lockFlag = redisDistLock.tryLockWithoutReleaseTime(lock, 1000);
            if (!lockFlag) {
                throw new MithrasException(CONCURRENT_OPERATION);
            }
            crRepayPlanDraftService.lambdaUpdate()
                    .eq(CrRepayPlanDraft::getCashFlowDate, dealTime.toLocalDate())
                    .set(CrRepayPlanDraft::getIsShow, YesOrNoNumberEnum.YES.getCode())
                    .update();
        } catch (Exception e) {
            log.error("修改出现错误", e);
        } finally {
            redisDistLock.unlock(lock);
        }
        log.info("征信修改到应还日期期项计划展示标志, time:{}", LocalDateTimeUtil.format(LocalDateTime.now(), FORMAT));
    }

}
