package cn.zswltech.mithras.foundation.auth;

/**
 * Checks whether current user is allowed to operate data by creator ownership.
 */
public interface DataAuthCreatorGuard {

    void check(DataAuthBusinessModule businessModule, Long mainId);
}
