package cn.zswltech.mithras.customer.application.client.copyhandler;

import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.application.client.bo.ClientCopyInfoBO;

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
