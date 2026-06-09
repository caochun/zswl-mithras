package cn.zswltech.mithras.service.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public interface ClientNameResolver {

    Map<Long, String> clientId2Name(Collection<Long> clientIds);

    default String clientId2NameSingle(Long clientId) {
        return clientId2Name(Collections.singletonList(clientId)).get(clientId);
    }
}
