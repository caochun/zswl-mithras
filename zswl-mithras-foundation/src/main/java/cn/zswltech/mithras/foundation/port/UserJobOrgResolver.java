package cn.zswltech.mithras.foundation.port;

import java.util.List;

/**
 * Resolves organization ids where a user holds a specific job.
 */
public interface UserJobOrgResolver {

    List<Long> userOrgIdsByJob(Long userId, String jobCode);
}
