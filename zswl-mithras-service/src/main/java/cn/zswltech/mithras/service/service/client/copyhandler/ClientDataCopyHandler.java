package cn.zswltech.mithras.service.service.client.copyhandler;

import cn.zswltech.mithras.service.enums.InfoModule;
import cn.zswltech.mithras.service.service.bo.ClientCopyInfoBO;

/**
 * @author dingqi
 * @date 2024/9/11
 * @description
 */
public interface ClientDataCopyHandler {
    void copyFromNewToOld(ClientCopyInfoBO clientCopyInfoBO);

    void copyFromOldToNew(ClientCopyInfoBO clientCopyInfoBO);

    void copyFromNewToNew(ClientCopyInfoBO clientCopyInfoBO);

    InfoModule getInfoModule();
}
