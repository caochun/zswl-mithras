package cn.zswltech.mithras.foundation.auth;

/**
 * Checks whether business data is editable under workflow constraints.
 */
public interface DataAuthProcessGuard {

    void check(DataAuthBusinessModule businessModule, Long mainId);
}
