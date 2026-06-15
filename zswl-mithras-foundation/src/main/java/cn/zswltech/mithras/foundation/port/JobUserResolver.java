package cn.zswltech.mithras.foundation.port;

import java.util.List;
import java.util.Set;

/**
 * Resolves users by job codes for shared business modules.
 */
public interface JobUserResolver {

    List<Long> jobUsers(Set<String> jobCodes);

    default List<Long> jobUsers(String jobCode) {
        return jobUsers(java.util.Collections.singleton(jobCode));
    }
}
