package cn.zswltech.mithras.service.flow.dynamicform.projreview;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.workflow.domain.enums.FlowDynamicFormEnum;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 董事会会议纪要
 *
 * @author wangchuanhao
 * @date 2022/8/8 11:44 AM
 */
@Component
public class SetDirectorMeetingFileHandler implements DynamicFormHandler {

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {

    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
    }

    @Override
    public void collect(TaskDetailRSP rsp) {

    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.projReview_setDirectorMeetingFile;
    }

}
