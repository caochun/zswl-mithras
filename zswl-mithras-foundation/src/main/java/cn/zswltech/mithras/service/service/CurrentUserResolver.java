package cn.zswltech.mithras.service.service;

/**
 * Resolves current authenticated user for shared business modules.
 */
public interface CurrentUserResolver {

    Long currentUserId();
}
