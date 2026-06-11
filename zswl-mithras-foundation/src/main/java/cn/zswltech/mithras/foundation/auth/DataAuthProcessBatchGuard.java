package cn.zswltech.mithras.foundation.auth;

import java.util.Collection;

/**
 * Checks whether business data is batch-editable under workflow constraints.
 */
public interface DataAuthProcessBatchGuard {

    void check(DataAuthBusinessModule businessModule, Collection<Long> mainIds);
}
