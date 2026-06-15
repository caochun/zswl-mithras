package cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.assetclassify;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AssetClassifyQualitativeAdjustHandler implements DynamicFormHandler {

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
        return FlowDynamicFormEnum.asset_classify_qualitative_adjust;
    }
}
