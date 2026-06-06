package cn.zswltech.mithras.customer.job;

import cn.zswltech.mithras.customer.application.client.ClientTimedTransferService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ClientTransferJob {

    @Autowired
    private ClientTimedTransferService clientTransferService;

    /**
     * 1、客户移交，正式移交
     */
    @XxlJob("timedClientTransfer")
    public void demoJobHandler() {
        try {
            log.info("timedClientTransfer 开始扫描");
            clientTransferService.timedPass();
            log.info("timedClientTransfer 执行结束");
        } catch (Exception e) {
            log.error("客户移交任务失败");
        }
    }
}
