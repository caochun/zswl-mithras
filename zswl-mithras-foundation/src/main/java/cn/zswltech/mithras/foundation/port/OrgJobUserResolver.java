package cn.zswltech.mithras.foundation.port;

import java.util.List;

/**
 * Resolves user ids for a specific job in a specific organization.
 */
public interface OrgJobUserResolver {

    List<Long> orgJobUsers(Long orgId, String jobCode);
}
