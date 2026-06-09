package cn.zswltech.mithras.service.flow.dynamicform;

import cn.zswltech.mithras.workflow.domain.enums.FlowDynamicFormEnum;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程表单处理器
 *
 * @author wangchuanhao
 * @date 2022/8/8 11:34 AM
 */
@Component
public class DynamicFormHandlerFactory implements ApplicationContextAware  {

    private Map<FlowDynamicFormEnum, DynamicFormHandler> map = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Map<String, DynamicFormHandler> beans = context.getBeansOfType(DynamicFormHandler.class);
        beans.values().forEach(b -> {
            map.put(b.getType(), b);
        });
    }

    public DynamicFormHandler getHandler(String formCode) {
        return map.get(FlowDynamicFormEnum.getByName(formCode));
    }

}
