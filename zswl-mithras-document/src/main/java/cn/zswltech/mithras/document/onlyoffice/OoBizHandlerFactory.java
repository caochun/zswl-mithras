package cn.zswltech.mithras.document.onlyoffice;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * onlyoffice业务权限校验
 *
 * @author wangchuanhao
 * @date 2022/8/23 4:55 PM
 */
@Component
public class OoBizHandlerFactory implements ApplicationContextAware  {

    private Map<String, OoBizHandler> map = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Map<String, OoBizHandler> beans = context.getBeansOfType(OoBizHandler.class);
        beans.values().forEach(b -> {
            map.put(b.getBusinessModule(), b);
        });
    }

    public OoBizHandler getHandler(String formCode) {
        return map.get(formCode);
    }

}
