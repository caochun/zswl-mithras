package cn.zswltech.mithras.service.flow.dynamicform.projreview;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.form.SetReviewMeetingPlanDateREQ;
import cn.zswltech.mithras.dto.flow.form.SetReviewMeetingPlanDateRSP;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.service.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.service.others.MithrasException;
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
public class SetMeetingPlanDateHandler implements DynamicFormHandler {
    public static final String FLOW_PARAMETER_NAME = "reviewMeetingPlanDate";

    @Resource
    private FlowVariableApiService flowVariableApiService;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        if (Objects.isNull(formMap.get(getType().name()))) {
            throw new MithrasException("评审会预计召开日期不能为空");
        }
        SetReviewMeetingPlanDateREQ setProjectClassifyREQ = JSONObject.parseObject(JSON.toJSONString(formMap.get(getType().name())), SetReviewMeetingPlanDateREQ.class);
        if (Objects.isNull(setProjectClassifyREQ) || StrUtil.isBlank(setProjectClassifyREQ.getReviewMeetingPlanDate())) {
            throw new MithrasException("评审会预计召开日期不能为空");
        }
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        SetReviewMeetingPlanDateREQ setReviewMeetingPlanDateREQ = JSONObject.parseObject(JSON.toJSONString(formMap.get(getType().name())), SetReviewMeetingPlanDateREQ.class);
        if (Objects.isNull(setReviewMeetingPlanDateREQ) || StrUtil.isBlank(setReviewMeetingPlanDateREQ.getReviewMeetingPlanDate())) {
            return;
        }
        flowVariableApiService.setVariables(taskResp.getProcessInstanceId(), MapUtil.of(FLOW_PARAMETER_NAME, setReviewMeetingPlanDateREQ.getReviewMeetingPlanDate()));
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        Map<String, Object> map = flowVariableApiService.getVariables(rsp.getProcessInstanceId(), Collections.singletonList(FLOW_PARAMETER_NAME));
        SetReviewMeetingPlanDateRSP setReviewMeetingPlanDateRSP = new SetReviewMeetingPlanDateRSP();
        if (Objects.nonNull(map.get(FLOW_PARAMETER_NAME))) {
            setReviewMeetingPlanDateRSP.setReviewMeetingPlanDate(map.get(FLOW_PARAMETER_NAME).toString());
        }
        rsp.getDynamicFormData().put(this.getType().name(), setReviewMeetingPlanDateRSP);
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.projReview_setReviewMeetingPlanDate;
    }
}
