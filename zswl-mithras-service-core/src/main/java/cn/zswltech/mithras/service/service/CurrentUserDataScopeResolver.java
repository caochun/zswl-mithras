package cn.zswltech.mithras.service.service;

import java.util.List;

/**
 * Resolves current user's data scope for shared business modules.
 */
public interface CurrentUserDataScopeResolver {

    /**
     * @return null means all departments are visible, empty means none are visible.
     */
    List<Long> canViewDeptIds();
}
