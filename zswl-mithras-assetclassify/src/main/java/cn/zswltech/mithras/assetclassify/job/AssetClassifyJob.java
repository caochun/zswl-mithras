package cn.zswltech.mithras.assetclassify.job;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.assetclassify.application.job.AssetClassifyInitJobService;
import cn.zswltech.mithras.assetclassify.application.job.AssetClassifyReviewAutoPassJobService;
import cn.zswltech.mithras.assetclassify.application.job.AssetClassifyWeekdayRemindJobService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2023/1/3
 * @description 资产五级分类任务
 */
@Slf4j
@Component
public class AssetClassifyJob {

    @Resource
    private AssetClassifyReviewAutoPassJobService assetClassifyReviewAutoPassJobService;
    @Resource
    private AssetClassifyInitJobService assetClassifyInitJobService;
    @Resource
    private AssetClassifyWeekdayRemindJobService assetClassifyWeekdayRemindJobService;

    @XxlJob("assetClassifyReviewAutoPass")
    public void reviewAutoPass() {
        assetClassifyReviewAutoPassJobService.reviewAutoPass(XxlJobHelper.getJobParam());
    }

    @Transactional(rollbackFor = Throwable.class)
    @XxlJob("assetClassifyInit")
    public void init() {
        LocalDateTime targetDateTime;
        String jobParam = XxlJobHelper.getJobParam();
        if (StrUtil.isBlank(jobParam)) {
            targetDateTime = LocalDateTime.now();
        } else {
            targetDateTime = LocalDateTimeUtil.parse(jobParam, DatePattern.NORM_DATETIME_PATTERN);
        }
        assetClassifyInitJobService.init(targetDateTime);
    }

    @Transactional(rollbackFor = Throwable.class)
    @XxlJob("assetClassifyWeekdayRemind")
    public void weekdayRemind() {
        String param = null;
        Integer day = 0;
        try {
            param = XxlJobHelper.getJobParam();
            day = Integer.valueOf(param);
        } catch (Exception e) {
            log.warn("assetClassifyWeekdayRemind get day error param : {}", param, e);
        }
        log.info("job assetClassifyInit began day {}", param);
        assetClassifyWeekdayRemindJobService.weekdayRemind(day);
        log.info("job assetClassifyInit over");
    }
}
