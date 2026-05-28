package cn.zswltech.mithras.service.job;

import cn.zswltech.mithras.service.service.groupcreditestablish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 项目状态维护job
 **/
@Slf4j
@Component
public class ProjectStatusJob {

    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private ProjReviewService projReviewService;
    @Resource
    private GroupCreditEstablishBaseInfoService groupCreditEstablishBaseInfoService;
    @Resource
    private GroupCreditReviewBaseInfoService groupCreditReviewBaseInfoService;

    /**
     * 维护项目/授信相关立项/评审数据
     **/
    @XxlJob("projStatusJob")
    public void projStatusJob() {
        try {
            //项目立项
            projEstablishBaseInfoService.doExpire(90);
            //项目评审
            projReviewService.doExpire(365);
            //授信立项
            groupCreditEstablishBaseInfoService.doExpire(90);
            //授信评审
            groupCreditReviewBaseInfoService.doExpire(365);
        } catch (Exception e) {
            log.warn("维护项目/授信相关立项/评审数据异常", e);
        }

    }

}
