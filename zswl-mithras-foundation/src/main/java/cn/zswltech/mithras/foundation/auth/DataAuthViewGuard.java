package cn.zswltech.mithras.foundation.auth;

/**
 * Checks whether current user can view business data.
 */
public interface DataAuthViewGuard {

    void check(DataAuthBusinessModule businessModule, Long mainId);
}
