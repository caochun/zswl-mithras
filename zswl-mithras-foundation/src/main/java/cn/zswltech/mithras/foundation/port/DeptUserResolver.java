package cn.zswltech.mithras.foundation.port;

import java.util.Set;

/**
 * Resolves active user ids by department code for shared business modules.
 */
public interface DeptUserResolver {

    Set<Long> userIdsByDeptCode(String deptCode);
}
