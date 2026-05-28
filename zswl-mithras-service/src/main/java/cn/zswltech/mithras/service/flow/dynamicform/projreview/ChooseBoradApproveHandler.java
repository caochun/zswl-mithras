package cn.zswltech.mithras.service.flow.dynamicform.projreview;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.service.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.enums.ProcessVarEnum;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.service.others.MithrasException;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2025/1/21
 * @description
 */
@Component
public class ChooseBoradApproveHandler implements DynamicFormHandler {
    @Resource
    private FlowVariableApiService flowVariableApiService;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        FormData formData = this.parse(formMap);
        if (Objects.isNull(formData) || Objects.isNull(formData.getNeedBorad())) {
            throw new MithrasException("<是否需经董事会审批>不能为空");
        }
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        FormData formData = this.parse(formMap);
        boolean var;
        if (Objects.nonNull(formData) && Objects.nonNull(formData.getNeedBorad())) {
            var = formData.getNeedBorad();
        } else {
            var = false;
        }
        flowVariableApiService.setVariables(taskResp.getProcessInstanceId(), MapUtil.of(ProcessVarEnum.projReviewChooseBorad.name(), var));
    }

    @Override
    public void collect(TaskDetailRSP rsp) {

    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.projReview_setNeedBorad;
    }

    @Data
    public static class FormData {
        private Boolean needBorad;
    }

    private FormData parse(Map<String, Object> formMap) {
        Object obj = formMap.get(this.getType().name());
        if (Objects.isNull(obj)) {
            return null;
        }
        return JSONUtil.toBean(JSONUtil.toJsonStr(obj), FormData.class);
    }
}
