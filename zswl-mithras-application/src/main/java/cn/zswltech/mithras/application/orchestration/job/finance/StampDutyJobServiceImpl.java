package cn.zswltech.mithras.application.orchestration.job.finance;

import cn.zswltech.mithras.finance.application.job.StampDutyJobService;
import cn.zswltech.mithras.application.orchestration.finance.stampduty.ReportStampDutyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 印花税跑批
 * @author: luyujie
 * @date: 2026/01/15 3:47 下午
 **/
@Slf4j
@Component
public class StampDutyJobServiceImpl implements StampDutyJobService {
    @Resource
    private ReportStampDutyService stampDutyService;


    /**
     * 每天定时增量更新起租的印花税
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void refreshStampDuty(String jobParam) {
        stampDutyService.refreshStampDuty(jobParam);
    }

}
