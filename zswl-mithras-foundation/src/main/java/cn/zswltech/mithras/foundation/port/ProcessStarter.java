package cn.zswltech.mithras.foundation.port;

/**
 * Starts workflow processes for business modules without exposing flow engine types.
 */
public interface ProcessStarter {

    String start(String businessKey, Long startUserId, String modelKey, String processInstanceName);
}
