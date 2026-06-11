package cn.zswltech.mithras.foundation.port;

/**
 * Resolves workflow process starter for shared business modules.
 */
public interface ProcessStartUserResolver {

    Long processStartUserId(String processId);
}
