package cn.zswltech.mithras.message.service.factory;

import cn.zswltech.mithras.message.service.AbstractMessageService;
import cn.zswltech.mithras.message.service.MessageService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName MessageFactory
 * @Description 消息工厂类，用于获取自定义消息处理方法
 * @Author jackerhe
 * @Date 2022/7/26 5:02 下午
 * @Version 1.0
 **/
@Component
public class MessageFactory implements ApplicationContextAware {
    private static Map<String, MessageService> beanMap = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, MessageService> map = applicationContext.getBeansOfType(MessageService.class);
        map.forEach((key,value)->beanMap.put(value.getType(),value));
    }
    //  直接调用即可
    public  <T extends AbstractMessageService> T chooseBean(String dbType){
        return (T) beanMap.get(dbType);
    }

}
