package cn.zswltech.mithras.workflow.trackevent;

import cn.zswltech.mithras.api.trackevent.TrackEventApi;
import cn.zswltech.mithras.dto.trackevent.TrackEventMainREQ;

import javax.servlet.ServletOutputStream;

public interface TrackEventApplicationService extends TrackEventApi {

    void download(ServletOutputStream outputStream, TrackEventMainREQ req);

    @Override
    default void download(TrackEventMainREQ req) {
        throw new UnsupportedOperationException("HTTP download is handled by TrackEventController");
    }
}
