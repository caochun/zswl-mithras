package cn.zswltech.mithras.workflow.application.trackevent;

import cn.zswltech.mithras.api.trackEvent.TrackEventApi;
import cn.zswltech.mithras.dto.trackEvent.TrackEventMainREQ;

import javax.servlet.ServletOutputStream;

public interface TrackEventApplicationService extends TrackEventApi {

    void download(ServletOutputStream outputStream, TrackEventMainREQ req);

    @Override
    default void download(TrackEventMainREQ req) {
        throw new UnsupportedOperationException("HTTP download is handled by TrackEventController");
    }
}
