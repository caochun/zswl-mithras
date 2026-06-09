package cn.zswltech.mithras.service.flow.file;

import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.repository.PlatformApiHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * 流程中 动态表单 处理文件
 *
 * @author wangchuanhao
 * @date 2022/11/23 11:23 AM
 */
@Component
public class FileHandlerFactory implements ApplicationContextAware {

    private Map<BusinessModuleEnum, IFileHandler> handlerMap = new EnumMap<>(BusinessModuleEnum.class);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, IFileHandler> beans = applicationContext.getBeansOfType(IFileHandler.class);
        for (IFileHandler fileHandler : beans.values()) {
            if (handlerMap.containsKey(fileHandler.businessModule())) {
                throw new RuntimeException("重复注册fileHandler,businessModule:" + fileHandler.businessModule().name());
            }
            handlerMap.put(fileHandler.businessModule(), fileHandler);
        }
    }

    public IFileHandler getFileHandler(BusinessModuleEnum businessModuleEnum){
        return handlerMap.get(businessModuleEnum);
    }


}
