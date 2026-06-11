package cn.zswltech.mithras.application.orchestration.workflow.flow.file;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2024/2/5
 * @description
 */
@Component
public class FlowFocusFileSelectorFactory implements ApplicationContextAware {
    private static final Map<ProcessModelTypeEnum, FlowFocusFileSelector> map = new HashMap<>();

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        Map<String, FlowFocusFileSelector> beanMap = applicationContext.getBeansOfType(FlowFocusFileSelector.class);
        if (CollectionUtil.isEmpty(beanMap)) {
            return;
        }
        for (Map.Entry<String, FlowFocusFileSelector> entry : beanMap.entrySet()) {
            FlowFocusFileSelector bean = entry.getValue();
            if (Objects.isNull(bean.processType())) {
                throw new MithrasException("流程模型类型不能为空");
            }
            map.put(bean.processType(), bean);
        }
    }

    public FlowFocusFileSelector getInstance(ProcessModelTypeEnum processModelTypeEnum) {
        FlowFocusFileSelector flowFocusFileSelector = map.get(processModelTypeEnum);
        if (Objects.isNull(flowFocusFileSelector)) {
            throw new MithrasException("未定义的流程模型");
        }
        return flowFocusFileSelector;
    }
}
