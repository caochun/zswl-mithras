package cn.zswltech.mithras.application.orchestration.adapter.afterlease.workflow.dynamicform;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckWayEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanClientService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author bigbear
 * @version 1.0
 * @description
 * @since 2025/9/9 16:36
 **/
@Slf4j
@Component
public class AfterLeaseCheckPlanHandler implements DynamicFormHandler {

    @Resource
    private AfterLeaseCheckPlanClientService afterLeaseCheckPlanClientService;

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        if (!CharSequenceUtil.equalsAny(taskResp.getTaskActivityId(), "userTask_assetManager", "assetManagementReview")){
            return;
        }
        Object obj = formMap.get(getType().name());
        if (Objects.isNull(obj)) {
            throw new MithrasException("请填写【后续检查计划维护】表单");
        }
        try {
            FormData formData = JSONUtil.toBean(JSONUtil.toJsonStr(obj), FormData.class);
            if (Objects.isNull(formData) || Objects.isNull(formData.getNextCheckWay())) {
                throw new MithrasException("下次租后检查形式不能为空");
            }
            // 校验下次租后检查类型
            AfterLeaseCheckWayEnum afterLeaseCheckWayEnum = AfterLeaseCheckWayEnum.find(formData.getNextCheckWay());
            if (Objects.isNull(afterLeaseCheckWayEnum)) {
                throw new MithrasException("下次租后检查形式填写错误");
            }
            if (!AfterLeaseCheckWayEnum.WITHOUT_CHECK.equals(afterLeaseCheckWayEnum) && Objects.isNull(formData.getNextDeadline())) {
                throw new MithrasException("[下次租后检查截止日]不能为空");
            }
            NewAfterLeaseCheckPlanClient planClient = afterLeaseCheckPlanClientService.getById(Long.valueOf(taskResp.getBusinessKey()));
            if (Objects.isNull(planClient)) {
                return;
            }
            // 需要保存数据，后续job中需要排除这个数据
            afterLeaseCheckPlanClientService.lambdaUpdate()
                    .eq(NewAfterLeaseCheckPlanClient::getId, planClient.getId())
                    .set(NewAfterLeaseCheckPlanClient::getTmpNextCheckWay, formData.getNextCheckWay())
                    .set(NewAfterLeaseCheckPlanClient::getTmpNextDeadline, formData.getNextDeadline())
                    .update();
        } catch (Exception e) {
            log.error("表单数据转换异常[formMap: {}]", JSONUtil.toJsonStr(formMap), e);
            throw new MithrasException("【后续检查计划维护】表单数据转换异常, 请检查！");
        }
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        Map<String, Object> dynamicFormData = rsp.getDynamicFormData();
        if (CollUtil.isEmpty(dynamicFormData)) {
            dynamicFormData = new HashMap<>();
        }
        NewAfterLeaseCheckPlanClient planClient = afterLeaseCheckPlanClientService.getById(Long.valueOf(rsp.getBusinessKey()));
        if (Objects.isNull(planClient)) {
            return;
        }
        FormData formData = new FormData();
        formData.setNextCheckWay(planClient.getTmpNextCheckWay());
        formData.setNextDeadline(planClient.getTmpNextDeadline());
        dynamicFormData.put(getType().name(), formData);
        rsp.setDynamicFormData(dynamicFormData);
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.follow_up_rental_inspection_form;
    }

    @Data
    public static class FormData {
        /**
         * 下次检查方式
         */
        private String nextCheckWay;

        /**
         * 下次租后检查截止日
         */
        private LocalDate nextDeadline;
    }
}
