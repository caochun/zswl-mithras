package cn.zswltech.mithras.projectprocess.job.notice;

import cn.zswltech.mithras.projectprocess.job.service.ProjReviewNoticeJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @description 项目立项提醒评审定时任务
 */
@Slf4j
@Component
public class ProjReviewNoticeJob {

    @Resource
    private ProjReviewNoticeJobService projReviewNoticeJobService;

    @XxlJob("projReviewNotice")
    public void contractStartRentRemindJobHandler() {
        projReviewNoticeJobService.contractStartRentRemindJobHandler();
    }
}
