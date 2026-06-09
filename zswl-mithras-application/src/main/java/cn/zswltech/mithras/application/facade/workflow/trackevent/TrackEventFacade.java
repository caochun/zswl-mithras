package cn.zswltech.mithras.application.facade.workflow.trackevent;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.UserRSP;
import cn.zswltech.mithras.dto.trackEvent.*;
import cn.zswltech.mithras.service.service.trackEvent.TrackEventService;
import cn.zswltech.mithras.workflow.application.trackevent.TrackEventApplicationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.util.List;

@Service
public class TrackEventFacade implements TrackEventApplicationService {

    @Resource
    private TrackEventService trackEventService;

    @Override
    public R<PageR<TrackEventListRSP>> list(TrackEventListREQ req) {
        return R.ok(trackEventService.queryList(req));
    }

    @Override
    public R<TrackEventDetailRSP> detail(Long id) {
        return R.ok(trackEventService.detail(id));
    }

    @Override
    public R<TrackEventContractInfoRSP> contractInfo(TrackEventContractInfoREQ req) {
        return R.ok(trackEventService.contractInfo(req));
    }

    @Override
    public R<Boolean> add(TrackEventAddREQ rsp) {
        return R.ok(trackEventService.add(rsp));
    }

//    @Override
//    public R<Boolean> update(TrackEventUpdateREQ rsp) {
//        return R.ok(trackEventService.edit(rsp));
//    }

    @Override
    public R<Boolean> close(Long id) {
        return R.ok(trackEventService.close(id));
    }

    @Override
    public R<List<UserRSP>> queryProcess() {
        return R.ok(trackEventService.queryProcess());
    }

    @Override
    public R<List<String>> contractCodeList() {
        return R.ok(trackEventService.contractCodeList());
    }

    @Override
    public R<List<String>> projNameList() {
        return null;
    }

    @Override
    public void download(ServletOutputStream outputStream, TrackEventMainREQ req) {
        trackEventService.download(outputStream, req);
    }

    @Override
    public R<Void> testEffect() {
        trackEventService.startEffect();
        return R.ok();
    }


}
