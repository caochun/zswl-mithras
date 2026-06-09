package cn.zswltech.mithras.service.service;

import java.util.Collection;
import java.util.Map;

/**
 * Resolves system user ids to display names for shared domain services.
 */
public interface UserNameResolver {

    Map<Long, String> sysUserId2Name(Collection<Long> userIds);

    default String sysUserId2NameSingle(Long userId) {
        return sysUserId2Name(java.util.Collections.singletonList(userId)).get(userId);
    }
}
