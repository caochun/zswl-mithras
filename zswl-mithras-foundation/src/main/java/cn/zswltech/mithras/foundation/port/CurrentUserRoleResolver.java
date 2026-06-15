package cn.zswltech.mithras.foundation.port;

import java.util.List;

/**
 * Resolves current user's role codes for shared business modules.
 */
public interface CurrentUserRoleResolver {

    List<String> currentUserRoles();
}
