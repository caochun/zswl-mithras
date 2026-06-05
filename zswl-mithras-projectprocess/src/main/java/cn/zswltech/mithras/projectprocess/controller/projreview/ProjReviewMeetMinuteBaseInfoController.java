package cn.zswltech.mithras.projectprocess.controller.projreview;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.projreview.meet.*;
import cn.zswltech.mithras.dto.trackEvent.TrackEventListRSP;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.api.projreview.ProjReviewMeetMinuteBaseInfoApi;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewMeetMinuteBaseInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class ProjReviewMeetMinuteBaseInfoController implements ProjReviewMeetMinuteBaseInfoApi {
    @Resource
    private ProjReviewMeetMinuteBaseInfoApplicationService projReviewMeetMinuteBaseInfoApplicationService;

    @Override
    public R<ProjReviewMeetMinuteBaseInfoDetailRSP> detail(ProjReviewMeetMinuteBaseInfoDetailREQ req) {
        return projReviewMeetMinuteBaseInfoApplicationService.detail(req);
    }

    @Override
    public R<Void> modify(ProjReviewMeetMinuteBaseInfoModifyREQ req) {
        return projReviewMeetMinuteBaseInfoApplicationService.modify(req);
    }

    @Override
    public R<Void> submit(ProjReviewMeetMinuteBaseInfoModifyREQ req) {
        return projReviewMeetMinuteBaseInfoApplicationService.submit(req);
    }

    @Override
    public R<List<ClientInfo>> getRelatedCustomers(ProjReviewMeetMinuteRelatedCustomersREQ req) {
        return projReviewMeetMinuteBaseInfoApplicationService.getRelatedCustomers(req);
    }

    @Override
    public R<ProjReviewMeetMinuteCreditDateCheckRSP> creditDateCheck(ProjReviewMeetMinuteBaseInfoDetailREQ req) {
        return projReviewMeetMinuteBaseInfoApplicationService.creditDateCheck(req);
    }

    @Override
    public R<List<TrackEventListRSP>> trackEventList(ProjReviewMeetMinuteBaseInfoDetailREQ req) {
        return projReviewMeetMinuteBaseInfoApplicationService.trackEventList(req);
    }
}
