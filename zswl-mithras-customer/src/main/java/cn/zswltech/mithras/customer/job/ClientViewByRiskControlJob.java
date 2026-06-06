package cn.zswltech.mithras.customer.job;

import cn.zswltech.mithras.customer.application.client.ClientViewByRiskControlJobService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class ClientViewByRiskControlJob {

    @Resource
    private ClientViewByRiskControlJobService clientViewByRiskControlJobService;

    @XxlJob("syncMonitoringClientsJob")
    public void syncMonitoringClientsJob() {
        String jobParam = XxlJobHelper.getJobParam();
        try {
            XxlJobHelper.log("开始执行监控客户数据同步任务，参数: {}", jobParam);
            boolean syncSuccess = clientViewByRiskControlJobService.syncMonitoringClients(jobParam);
            if (syncSuccess) {
                XxlJobHelper.handleSuccess("监控客户数据同步成功");
                log.info("监控客户数据同步任务执行成功");
            } else {
                XxlJobHelper.handleFail("监控客户数据同步失败");
                log.error("监控客户数据同步任务执行失败");
            }
        } catch (Exception e) {
            XxlJobHelper.log("监控客户数据同步任务异常: {}", e.getMessage());
            XxlJobHelper.handleFail("监控客户数据同步异常: " + e.getMessage());
            log.error("监控客户数据同步任务异常", e);
        }
    }
}
