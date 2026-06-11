package cn.zswltech.mithras.assetclassify.flow.dynamicform;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 表单处理器
 *
 * 设置评审会会议纪要
 */
@Component
public class AssetClassifyQualitativeAdjustHandler implements DynamicFormHandler {

   /* @Resource
    private AssetClassifyClientService classifyClientService;*/

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        /*AssetClassifyClient assetClassifyClient = classifyClientService.getById(Long.valueOf(taskResp.getBusinessKey()));
        assetClassifyClient.setQualitativeAdjust((Integer) formMap.get(getType().name()));
        classifyClientService.updateById(assetClassifyClient);*/
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.asset_classify_qualitative_adjust;
    }

}
