package cn.zswltech.mithras.service.flow.listener.endhandler.contract;

import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 合同流程结束处理器工厂
 *
 * @author wangchuanhao
 * @date 2022/12/15 11:22 AM
 */
@Component
public class ContractProcessEndWorkerFactory implements ApplicationContextAware {

    private Map<String, AbstractContractProcessEndWorker> workerMap = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, AbstractContractProcessEndWorker> springWorkerBeanMap = applicationContext.getBeansOfType(AbstractContractProcessEndWorker.class);
        for (AbstractContractProcessEndWorker worker : springWorkerBeanMap.values()) {
            for (ProcessModelTypeEnum modelTypeEnum : worker.handleModelTypeList()) {
                workerMap.put(modelTypeEnum.name(), worker);
            }
        }
    }

    public AbstractContractProcessEndWorker getWorker(String modelKey) {
        return workerMap.get(modelKey);
    }

}
