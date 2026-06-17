package cn.zswltech.mithras.third.datashare.application.port;

import java.util.Map;

public interface DataShareClientCodeSyncPort {

    void syncClientCodes(Map<String, String> clientCodeMap);
}
