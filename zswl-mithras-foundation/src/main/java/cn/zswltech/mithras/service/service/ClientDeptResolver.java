package cn.zswltech.mithras.service.service;

import java.util.Collection;
import java.util.Map;

public interface ClientDeptResolver {

    Map<Long, Long> clientId2DeptId(Collection<Long> clientIds);
}
