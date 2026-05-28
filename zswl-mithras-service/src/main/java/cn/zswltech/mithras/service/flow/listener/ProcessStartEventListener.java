package cn.zswltech.mithras.service.flow.listener;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.flow.core.extension.event.ProcessStartEvent;
import cn.zswltech.flow.core.extension.event.context.ProcessStartContext;
import cn.zswltech.mithras.service.constant.FlowConstants;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.service.flow.FlowQueryExtraService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author dingqi
 * @date 2022/10/24
 * @description 流程开始监听器
 */
@Slf4j
@Component
public class ProcessStartEventListener implements ApplicationListener<ProcessStartEvent> {
    @Resource
    private FlowVariableApiService flowVariableApiService;

    @Override
    public void onApplicationEvent(ProcessStartEvent processStartEvent) {
        log.info("监听到流程开始, 数据: {}", JSONUtil.toJsonStr(processStartEvent.getProcessStartContext()));
        ProcessStartContext context = processStartEvent.getProcessStartContext();
        if (CharSequenceUtil.equalsAny(context.getModelKey(),
                ProcessModelTypeEnum.ProjReviewCreateFlow.name(),
                ProcessModelTypeEnum.ProjReviewModifyFlow.name(),
                ProcessModelTypeEnum.AfterLeaseExtendFlow.name(),
                ProcessModelTypeEnum.AfterLeaseRepaymentFlow.name(),
                ProcessModelTypeEnum.GroupCreditReviewCreateFlow.name(),
                ProcessModelTypeEnum.GroupCreditReviewModifyFlow.name()
        )) {
            // 设置参数
            Map<String, Object> params = new HashMap<>();
            params.put(FlowConstants.PROJ_REVIEW_IS_REVIEW_MEETING_BACK, Boolean.FALSE);
            params.put(FlowConstants.PROJ_REVIEW_IS_START_USER_RECONSIDERATION, Boolean.FALSE);
            flowVariableApiService.setVariables(context.getProcessInstanceId(), params);
        }
        //构建流程中间表
        getBean(FlowQueryExtraService.class).process(processStartEvent);
    }
}
