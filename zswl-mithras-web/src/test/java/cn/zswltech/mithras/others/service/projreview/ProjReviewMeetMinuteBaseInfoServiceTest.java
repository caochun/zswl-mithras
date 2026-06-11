package cn.zswltech.mithras.others.service.projreview;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewMeetMinuteBaseInfoService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2022/9/6
 * @description
 */
public class ProjReviewMeetMinuteBaseInfoServiceTest extends ApplicationTest {
    @Resource
    private ProjReviewMeetMinuteBaseInfoService projReviewMeetMinuteBaseInfoService;

    @Test
    public void initProjReviewMeetMinute() {
        projReviewMeetMinuteBaseInfoService.initProjReviewMeetMinute(3700L, "11090000");
    }


    @Test
    public void initChangeReviewMeetMinute() {
        projReviewMeetMinuteBaseInfoService.initChangeReviewMeetMinute(1337L, "5402587");
    }

}
