package cn.zswltech.mithras.message.service.job;

import cn.zswltech.mithras.message.application.job.MessageNoticeJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @description 通知相关定时任务
 */
@Slf4j
@Component
public class MessageNoticeJob {

    @Resource
    private MessageNoticeJobService messageNoticeJobService;

    @XxlJob("syncMessage2DB")
    public void syncMessage2DB() {
        messageNoticeJobService.syncMessage2DB();
    }
}
