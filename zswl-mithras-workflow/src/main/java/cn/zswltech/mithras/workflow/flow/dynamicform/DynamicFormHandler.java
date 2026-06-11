package cn.zswltech.mithras.workflow.flow.dynamicform;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;

import java.util.Map;

/**
 * 表单处理器
 *
 * @author wangchuanhao
 * @date 2022/8/8 11:35 AM
 */
public interface DynamicFormHandler {

    /**
     * 通过任务 提交数据的时候 做校验
     * @return
     */
    default void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {}

    /**
     * 通过任务 提交数据的时候 处理
     */
    void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt);

    /**
     * 返回前端时 收集数据 只有任务详情要返回表格
     * @return
     */
    void collect(TaskDetailRSP rsp);

    FlowDynamicFormEnum getType();

}
