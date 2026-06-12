package cn.zswltech.mithras.application.orchestration.job.riskcontrol;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.riskcontrol.opinion.RiskControlOpinionNoticeReq;
import cn.zswltech.mithras.third.datashare.persistence.model.DataShareManager;
import cn.zswltech.mithras.third.datashare.service.DataShareManagerService;
import cn.zswltech.mithras.riskcontrol.application.job.RiskControlJobService;
import cn.zswltech.mithras.riskcontrol.application.RiskControlOpinionMonitorApplicationService;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionHandleStatus;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitor;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitor;
import cn.zswltech.mithras.application.orchestration.riskcontrol.opinion.RiskControlOpinionMonitorService;
import cn.zswltech.mithras.application.orchestration.riskcontrol.opinion.RiskControlOpinionVersionService;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitorService;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RiskControlJobServiceImpl implements RiskControlJobService {


    @Resource
    private DataShareManagerService dataShareManagerService;
    @Resource
    private RiskControlOpinionMonitorApplicationService riskControlOpinionMonitorApplicationService;
    @Resource
    private RiskControlOpinionMonitorService riskControlOpinionMonitorService;
    @Resource
    private RiskControlOpinionVersionService riskControlOpinionVersionService;
    @Resource
    private RiskControlWarnMonitorService riskControlWarnMonitorService;


    private final static String MODULE_NAME = "risk_control_sync";
    private final static int batchSize = 1000; // 每批1000条

    /**
     * 1、增量同步风险数据
     */
    @Override
    public void syncRiskControl() {
        try {
            log.info(">>>>>>>>>>>>>>syncRiskControlHandler began syncMerchants");
            DataShareManager shareManager = dataShareManagerService.getOne(Wrappers.<DataShareManager>lambdaQuery()
                    .eq(DataShareManager::getModelName, MODULE_NAME)
                    .orderByDesc(DataShareManager::getId)
                    .last(StringUtil.mysqlLimitOne()));
            if(ObjectUtil.isEmpty(shareManager)){
                shareManager = new DataShareManager();
                shareManager.setModelName(MODULE_NAME);
                shareManager.setPageSize(1000);
                shareManager.setDataTotal(0);
                shareManager.setPageNum(1);
                dataShareManagerService.save(shareManager);
            }
            RiskControlOpinionNoticeReq req = new RiskControlOpinionNoticeReq();
            req.setSize(shareManager.getPageSize());
            req.setStartId(shareManager.getDataTotal());
            riskControlOpinionMonitorApplicationService.notice(req);
            RiskControlOpinionMonitor monitor = riskControlOpinionMonitorService.getOne(Wrappers.<RiskControlOpinionMonitor>lambdaQuery()
                    .orderByDesc(RiskControlOpinionMonitor::getId)
                    .last(StringUtil.mysqlLimitOne()));
            if(ObjectUtil.isNotEmpty(monitor)){
                shareManager.setDataTotal(Integer.parseInt(String.valueOf(monitor.getId())));
            }
            dataShareManagerService.updateById(shareManager);
            log.info(">>>>>>>>>>>>>>syncRiskControlHandler over syncMerchants");
        } catch (Exception e) {
            log.error("syncRiskControlHandler error", e);
        }

    }

    /**
     * 1、发起预警流程job
     */
    @Override
    public void startWarnFlow(LocalDate now) {
        try {
            log.info(">>>>>>>>>>>>>>startWarnFlowJob began");
            //riskControlWarnMonitorService.ignoreWarn();// 接通慧眼数据后，如果继续执行此任务，则需要去掉这行代码
            List<RiskControlWarnMonitor> list = riskControlWarnMonitorService.list(Wrappers.<RiskControlWarnMonitor>lambdaQuery()
                    .eq(RiskControlWarnMonitor::getHandleStatus, RiskControlOpinionHandleStatus.PEND_HANDLE.name())
                    .ge(RiskControlWarnMonitor::getCreateTime, now));
            if(ObjectUtil.isNotEmpty(list)) {
                // 接通慧眼数据后,如果继续执行此任务,需要分批执行审批流程,以防慧眼预警数据过多,对数据库造成压力
                List<List<RiskControlWarnMonitor>> batchList = Lists.partition(list, batchSize);
                for (List<RiskControlWarnMonitor> batch : batchList) {
                    riskControlOpinionVersionService.warnInitiateApproval(batch);
                }
            }
            log.info(">>>>>>>>>>>>>>startWarnFlowJob over ");
        } catch (Exception e) {
            log.error("startWarnFlowJob error", e);
        }

    }

    //舆情统一监测（已放款）流程超时提醒
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void riskControlPaymentFlow(String jobParam) {
        try {
            log.info("riskControlPaymentFlowJob start");
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            // jobParam = "2067";
            riskControlOpinionVersionService.riskRemind(jobParam);
            stopWatch.stop();
            log.info("riskControlPaymentFlowJob end!!! 耗时={}s", stopWatch.prettyPrint(TimeUnit.SECONDS));
        } catch (Exception e) {
            log.error("riskControlPaymentFlowJob 执行异常，e={}", e);
        }
    }

}
