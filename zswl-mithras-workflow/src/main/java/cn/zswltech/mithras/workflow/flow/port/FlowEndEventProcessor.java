package cn.zswltech.mithras.workflow.flow.port;

public interface FlowEndEventProcessor {

    void processEnd(Long id, Integer endType, Long startUserId, String processInstanceId, String modelKey);
}
