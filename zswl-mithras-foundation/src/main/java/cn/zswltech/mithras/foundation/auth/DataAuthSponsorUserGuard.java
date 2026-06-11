package cn.zswltech.mithras.foundation.auth;

/**
 * Checks whether current user can operate data owned by a sponsor user.
 */
public interface DataAuthSponsorUserGuard {

    void check(DataAuthBusinessModule businessModule, Long mainId);
}
