package cn.zswltech.mithras.fund.directfinancing.application.property;

import cn.zswltech.mithras.api.property.PutPopertyBaseInfoApi;
import cn.zswltech.mithras.dto.property.PutPropertyBaseInfoListREQ;

import javax.servlet.ServletOutputStream;

public interface PutPropertyBaseInfoApplicationService extends PutPopertyBaseInfoApi {

    void putPropertyListDownload(PutPropertyBaseInfoListREQ req, ServletOutputStream outputStream);

    @Override
    default void putPropertyListDownload(PutPropertyBaseInfoListREQ req) {
        throw new UnsupportedOperationException("HTTP download is handled by PutPropertyBaseInfoController");
    }
}
