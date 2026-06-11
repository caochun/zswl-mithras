package cn.zswltech.mithras.customer.application.client.copyhandler;

import cn.zswltech.mithras.customer.enums.InfoModule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dingqi
 * @date 2024/9/11
 * @description
 */
public class ClientDataCopyHandlerFactory {
    private static final Map<InfoModule, ClientDataCopyHandler> map = new ConcurrentHashMap<>();

    public static void register(ClientDataCopyHandler clientDataCopyHandler) {
        map.put(clientDataCopyHandler.getInfoModule(), clientDataCopyHandler);
    }

    public static ClientDataCopyHandler getInstance(InfoModule infoModule) {
        return map.get(infoModule);
    }
}
