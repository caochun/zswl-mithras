package cn.zswltech.mithras.margin.job;

import cn.zswltech.mithras.margin.application.job.DepositWriteOffJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 保证金抵扣/退还流程生成待办
 *
 * @author dingqi
 * @date 2025/10/27
 */
@Slf4j
@Component
public class DepositWriteOffJob {

    @Resource
    private DepositWriteOffJobService depositWriteOffJobService;

    @XxlJob("sendNoticeJob")
    public void sendNoticeJob() {
        depositWriteOffJobService.sendNoticeJob();
    }

    @XxlJob("depositWriteOffJob")
    public void depositWriteOffJob() {
        depositWriteOffJobService.depositWriteOffJob();
    }
}
