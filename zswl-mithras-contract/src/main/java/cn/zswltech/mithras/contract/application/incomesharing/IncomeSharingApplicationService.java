package cn.zswltech.mithras.contract.application.incomesharing;

import cn.zswltech.mithras.api.incomesharing.IncomeSharingApi;
import cn.zswltech.mithras.dto.incomesharing.IncomeSharingDetailREQ;
import cn.zswltech.mithras.dto.incomesharing.IncomeSharingListREQ;

import javax.servlet.ServletOutputStream;

public interface IncomeSharingApplicationService extends IncomeSharingApi {

    void incomeSharingDownload(IncomeSharingListREQ req, ServletOutputStream outputStream);

    void incomeSharingDetailDownload(IncomeSharingDetailREQ req, ServletOutputStream outputStream);

    @Override
    default void incomeSharingDownload(IncomeSharingListREQ req) {
        throw new UnsupportedOperationException("HTTP download is handled by IncomeSharingController");
    }

    @Override
    default void incomeSharingDetailDownload(IncomeSharingDetailREQ req) {
        throw new UnsupportedOperationException("HTTP download is handled by IncomeSharingController");
    }
}
