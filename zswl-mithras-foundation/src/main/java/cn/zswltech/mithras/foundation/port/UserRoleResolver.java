package cn.zswltech.mithras.foundation.port;

/**
 * Resolves user roles for shared business modules.
 */
public interface UserRoleResolver {

    boolean userIsSpecificRole(Long userId, String... roleCodes);
}
