package cn.zswltech.mithras.projectprocess.service;

import java.util.Collection;
import java.util.Map;

/**
 * Name lookup boundary used by project-process handlers.
 */
public interface ProjectProcessNameResolver {

    String clientId2NameSingle(Long clientId);

    Map<Long, String> clientId2Name(Collection<Long> clientIds);

    Map<Long, String> sysUserId2Name(Collection<Long> userIds);

    Map<Long, String> deptId2Name(Collection<Long> deptIds);
}
