package cn.zswltech.mithras.application.adapter.projectprocess;

import cn.zswltech.mithras.projectprocess.application.job.ProjectStatusSupportPort;
import cn.zswltech.mithras.service.service.groupcreditestablish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ProjectStatusSupportPortAdapter implements ProjectStatusSupportPort {

    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private ProjReviewService projReviewService;
    @Resource
    private GroupCreditEstablishBaseInfoService groupCreditEstablishBaseInfoService;
    @Resource
    private GroupCreditReviewBaseInfoService groupCreditReviewBaseInfoService;

    @Override
    public void expireProjEstablish(int days) {
        projEstablishBaseInfoService.doExpire(days);
    }

    @Override
    public void expireProjReview(int days) {
        projReviewService.doExpire(days);
    }

    @Override
    public void expireGroupCreditEstablish(int days) {
        groupCreditEstablishBaseInfoService.doExpire(days);
    }

    @Override
    public void expireGroupCreditReview(int days) {
        groupCreditReviewBaseInfoService.doExpire(days);
    }
}
