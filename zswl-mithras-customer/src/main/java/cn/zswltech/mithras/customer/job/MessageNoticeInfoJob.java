package cn.zswltech.mithras.customer.job;

import cn.zswltech.mithras.customer.application.client.ClientMessageNoticeJobService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MessageNoticeInfoJob {

    @Resource
    private ClientMessageNoticeJobService clientMessageNoticeJobService;


    /**
     * 工作台消息通知
     */
    @XxlJob("messageNoticeJob")
    public void MessageNoticeJob() {
        try {
            String jobParam = XxlJobHelper.getJobParam();
            clientMessageNoticeJobService.sendMessageNotice(jobParam);

        } catch (Exception e) {
            log.error("客户移交任务失败");
        }
    }

}
