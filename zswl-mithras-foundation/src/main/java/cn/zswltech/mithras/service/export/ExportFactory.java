package cn.zswltech.mithras.service.export;

import cn.zswltech.mithras.service.others.MithrasException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @ClassName ExportFactory
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/7/8 4:27 下午
 * @Version 1.0
 **/
@Service
public class ExportFactory implements ApplicationContextAware, InitializingBean {

    private ApplicationContext context;

    private Map<String, ExportHandle> handlerMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, ExportHandle> beans = context.getBeansOfType(ExportHandle.class);
        if(!CollectionUtils.isEmpty(beans)){
            for (ExportHandle platformApiHandler : beans.values()) {
                if (handlerMap.containsKey(platformApiHandler.getBusinessType())) {
                    throw new RuntimeException("重复注册ExportFactory,businessType:" + platformApiHandler.getBusinessType());
                }
                handlerMap.put(platformApiHandler.getBusinessType(), platformApiHandler);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public ExportHandle getHandler(String BusinessType){
        return Optional.ofNullable(handlerMap.get(BusinessType)).orElseThrow(() -> new MithrasException("未注册导出信息"));
    }
}
