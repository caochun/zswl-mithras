package cn.zswltech.mithras.customer.job;

import cn.zswltech.mithras.customer.application.client.ClientFocusOpinionSyncService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class SyncFocusOpinionToRiskSystem {

    @Resource
    private ClientFocusOpinionSyncService clientFocusOpinionSyncService;

    /**
     * 1、同步舆情关注客户列表到风控系统
     * 全量同步
     */
    @XxlJob("syncFocusOpinionToRiskSystem")
    public void jobHandler() {
        try {
            clientFocusOpinionSyncService.syncFocusOpinionToRiskSystem();
        } catch (Exception e) {
            log.error("2-同步舆情关注客户列表失败");
        }
    }
}
