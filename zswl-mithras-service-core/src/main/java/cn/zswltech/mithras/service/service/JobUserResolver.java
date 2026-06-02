package cn.zswltech.mithras.service.service;

import java.util.List;
import java.util.Set;

/**
 * Resolves users by job codes for shared business modules.
 */
public interface JobUserResolver {

    List<Long> jobUsers(Set<String> jobCodes);
}
