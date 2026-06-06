package cn.zswltech.mithras.collection.job;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.collection.application.job.CollectionPenaltyInterestJobService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/9/15
 * @description 收款相关定时任务
 */
@Slf4j
@Component
public class CollectionJob {
    @Resource
    private CollectionPenaltyInterestJobService collectionPenaltyInterestJobService;

    @XxlJob("penaltyInterestJobHandler")
    public void penaltyInterestJobHandler() {
        collectionPenaltyInterestJobService.penaltyInterestJobHandler();
    }

    @Deprecated
    public void penaltyInterestJobHandler2() {
        collectionPenaltyInterestJobService.penaltyInterestJobHandler2(resolveTargetDate());
    }

    @XxlJob("penaltyInterestJobHandler2")
    public void penaltyInterestJobHandler3() {
        collectionPenaltyInterestJobService.penaltyInterestJobHandler3(resolveTargetDate());
    }

    @XxlJob("overdueSnapshotJob")
    public void overdueSnapshot() {
        collectionPenaltyInterestJobService.overdueSnapshot();
    }

    @XxlJob("updateClientPenaltyInterest")
    public void updateClientPenaltyInterest() {
        collectionPenaltyInterestJobService.updateClientPenaltyInterest();
    }

    private LocalDate resolveTargetDate() {
        String param = XxlJobHelper.getJobParam();
        if (ObjectUtil.isEmpty(param)) {
            return LocalDate.now();
        }
        return LocalDateTimeUtil.parse(param, "yyyy-MM-dd").toLocalDate();
    }
}
