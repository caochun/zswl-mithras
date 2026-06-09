package cn.zswltech.mithras.service.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Resolves department ids to display names for shared domain services.
 */
public interface DeptNameResolver {

    Map<Long, String> deptId2Name(Collection<Long> deptIds);

    default String deptId2NameSingle(Long deptId) {
        return deptId2Name(Collections.singletonList(deptId)).get(deptId);
    }
}
