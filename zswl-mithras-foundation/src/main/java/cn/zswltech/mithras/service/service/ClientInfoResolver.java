package cn.zswltech.mithras.service.service;

import cn.zswltech.mithras.dto.client.client.ClientInfo;

import java.util.Collection;
import java.util.Map;

public interface ClientInfoResolver {

    Map<Long, ClientInfo> clientId2Client(Collection<Long> clientIds);
}
