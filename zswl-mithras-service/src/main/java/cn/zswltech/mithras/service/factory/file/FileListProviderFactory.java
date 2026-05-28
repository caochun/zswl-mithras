package cn.zswltech.mithras.service.factory.file;

import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 文件列表提供 每个模块一个
 *
 * @author wangchuanhao
 * @date 2023/2/6 10:48 AM
 */
@Component
public class FileListProviderFactory implements ApplicationContextAware {

    private Map<BusinessModuleEnum, AbstractFileListProvider> map = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Map<String, AbstractFileListProvider> beans = context.getBeansOfType(AbstractFileListProvider.class);
        beans.values().forEach(b -> {
            map.put(b.getBusinessModule(), b);
        });
    }

    public AbstractFileListProvider getProvider(String businessModule) {
        if ("PAYMENTPOLICY".equals(businessModule)){
            return map.get(BusinessModuleEnum.of("POLICY"));
        }
        return map.get(BusinessModuleEnum.of(businessModule));
    }

}
