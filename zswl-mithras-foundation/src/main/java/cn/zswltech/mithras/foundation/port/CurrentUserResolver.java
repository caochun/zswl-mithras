package cn.zswltech.mithras.foundation.port;

/**
 * Resolves current authenticated user for shared business modules.
 */
public interface CurrentUserResolver {

    Long currentUserId();
}
