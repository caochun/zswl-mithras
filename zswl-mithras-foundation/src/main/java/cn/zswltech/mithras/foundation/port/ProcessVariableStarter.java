package cn.zswltech.mithras.foundation.port;

import java.util.Map;

/**
 * Starts workflow processes with optional department and variable context.
 */
public interface ProcessVariableStarter {

    String start(String businessKey, Long startUserId, Long startUserDeptId, String modelKey,
                 String processInstanceName, Map<String, Object> variables);
}
