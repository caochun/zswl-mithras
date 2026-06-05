package cn.zswltech.mithras.finance.application.stampduty;

import cn.zswltech.mithras.api.stampduty.StampDutyApi;
import cn.zswltech.mithras.dto.stampduty.StampDutyBatchREQ;

import javax.servlet.ServletOutputStream;

public interface StampDutyApplicationService extends StampDutyApi {

    void export(StampDutyBatchREQ req, ServletOutputStream outputStream);

    @Override
    default void export(StampDutyBatchREQ req) {
        throw new UnsupportedOperationException("HTTP export is handled by StampDutyController");
    }
}
