package cn.zswltech.mithras.service.service.flow;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/9/5 19:38
 */
public interface FlowEndEventProcessor {

    /**
     * 处理结束时间
     *
     * @param id
     * @param endType
     * @param startUserId
     */
    void processEnd(Long id, Integer endType, Long startUserId, String processInstanceId, String modelKey);
}
