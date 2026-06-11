package cn.zswltech.mithras.assetclassify.flow.dynamicform;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.assetclassify.mapper.AssetClassifyClientMapper;
import cn.zswltech.mithras.assetclassify.model.AssetClassifyClient;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 表单处理器
 *
 * 设置评审会会议纪要
 */
@Component
public class AssetClassifyResultHandler implements DynamicFormHandler {

    @Resource
    private AssetClassifyClientMapper assetClassifyClientMapper;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        AssetClassifyClient assetClassifyClient = assetClassifyClientMapper.selectById(Long.valueOf(taskResp.getBusinessKey()));
        assetClassifyClient.setQualitativeAdjust((Integer) formMap.get(FlowDynamicFormEnum.asset_classify_qualitative_adjust.name()));
        if(YesOrNoNumberEnum.YES.getCode().equals(assetClassifyClient.getQualitativeAdjust()))
        {
            assetClassifyClient.setClassifyResult(formMap.get(getType().name()).toString());
        } else {
            assetClassifyClient.setClassifyResult(assetClassifyClient.getInitClassifyResult());
        }
        assetClassifyClientMapper.updateById(assetClassifyClient);
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        AssetClassifyClient assetClassifyClient = assetClassifyClientMapper.selectById(Long.valueOf(rsp.getBusinessKey()));
        rsp.getDynamicFormData().put(getType().name(), assetClassifyClient.getClassifyResult());
        rsp.getDynamicFormData().put(FlowDynamicFormEnum.asset_classify_qualitative_adjust.name(),assetClassifyClient.getQualitativeAdjust());
        rsp.getDynamicFormData().put(FlowDynamicFormEnum.asset_classify_init_result.name(), assetClassifyClient.getInitClassifyResult());
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.asset_classify_result;
    }

}
