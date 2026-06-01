package cn.zswltech.mithras.service.repository;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * 三方接口处理器工厂
 *
 * @author wang
 * @date 2022/4/11 7:10 PM
 */
@Service("platformApiHandleFactory")
public class PlatformApiHandleFactory implements ApplicationContextAware,InitializingBean {

    private ApplicationContext context;

    private Map<PlatformApiEnum, PlatformApiHandler> handlerMap = new HashMap<>();

    private Map<PlatformApiEnum, PlatformApiRequestInspector> requestInspectorMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, PlatformApiHandler> beans = context.getBeansOfType(PlatformApiHandler.class);
        if(!CollectionUtils.isEmpty(beans)){
            for (PlatformApiHandler platformApiHandler : beans.values()) {
                if (handlerMap.containsKey(platformApiHandler.platformApi())) {
                    throw new RuntimeException("重复注册platformApiHandler,platformApiCode:" + platformApiHandler.platformApi().apiCode);
                }
                handlerMap.put(platformApiHandler.platformApi(), platformApiHandler);
            }
        }

        Map<String, PlatformApiRequestInspector> requestInspectorBeans = context.getBeansOfType(PlatformApiRequestInspector.class);
        if(!CollectionUtils.isEmpty(requestInspectorBeans)){
            for (PlatformApiRequestInspector requestInspector : requestInspectorBeans.values()) {
                if (requestInspectorMap.containsKey(requestInspector.platformApi())) {
                    throw new RuntimeException("重复注册platformApiRequestInspector,platformApiCode:" + requestInspector.platformApi().apiCode);
                }
                requestInspectorMap.put(requestInspector.platformApi(), requestInspector);
            }
        }



    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public PlatformApiHandler getPlatformApiHandler(PlatformApiEnum platformApiEnum){

        return handlerMap.get(platformApiEnum);
    }

    public PlatformApiRequestInspector getPlatformApiRequestInspector(PlatformApiEnum platformApiEnum){
        return requestInspectorMap.get(platformApiEnum);
    }

    public PlatformApiHandler getPlatformApiHandlerByApiCode(String apiCode, String ownerPlatformName){
        PlatformApiEnum platformApi = PlatformApiEnum.getPlatformApiByApiCode(apiCode, ownerPlatformName);
        if (platformApi == null) {
            return null;
        }
        return handlerMap.get(platformApi);
    }
}
