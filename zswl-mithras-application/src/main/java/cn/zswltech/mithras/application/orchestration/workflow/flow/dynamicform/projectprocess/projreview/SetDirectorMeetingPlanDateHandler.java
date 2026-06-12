package cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.projectprocess.projreview;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.form.SetDirectorMeetingPlanDateREQ;
import cn.zswltech.mithras.dto.flow.form.SetDirectorMeetingPlanDateRSP;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/3/17
 * @description
 */
@Component
public class SetDirectorMeetingPlanDateHandler implements DynamicFormHandler {
    private static final String FLOW_PARAMETER_NAME = "directorMeetingPlanDate";

    @Resource
    private FlowVariableApiService flowVariableApiService;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        if (Objects.isNull(formMap.get(getType().name()))) {
            throw new MithrasException("董事会预计召开日期不能为空");
        }
        SetDirectorMeetingPlanDateREQ setDirectorMeetingPlanDateREQ = JSONObject.parseObject(JSON.toJSONString(formMap.get(getType().name())), SetDirectorMeetingPlanDateREQ.class);
        if (Objects.isNull(setDirectorMeetingPlanDateREQ) || StrUtil.isBlank(setDirectorMeetingPlanDateREQ.getDirectorMeetingPlanDate())) {
            throw new MithrasException("董事会预计召开日期不能为空");
        }
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        SetDirectorMeetingPlanDateREQ setDirectorMeetingPlanDateREQ = JSONObject.parseObject(JSON.toJSONString(formMap.get(getType().name())), SetDirectorMeetingPlanDateREQ.class);
        if (Objects.isNull(setDirectorMeetingPlanDateREQ) || StrUtil.isBlank(setDirectorMeetingPlanDateREQ.getDirectorMeetingPlanDate())) {
            return;
        }
        flowVariableApiService.setVariables(taskResp.getProcessInstanceId(), MapUtil.of(FLOW_PARAMETER_NAME, setDirectorMeetingPlanDateREQ.getDirectorMeetingPlanDate()));
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        Map<String, Object> map = flowVariableApiService.getVariables(rsp.getProcessInstanceId(), Collections.singletonList(FLOW_PARAMETER_NAME));
        SetDirectorMeetingPlanDateRSP setDirectorMeetingPlanDateRSP = new SetDirectorMeetingPlanDateRSP();
        if (Objects.nonNull(map.get(FLOW_PARAMETER_NAME))) {
            setDirectorMeetingPlanDateRSP.setDirectorMeetingPlanDate(map.get(FLOW_PARAMETER_NAME).toString());
        }
        rsp.getDynamicFormData().put(this.getType().name(), setDirectorMeetingPlanDateRSP);
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.projReview_setDirectorMeetingPlanDate;
    }
}
