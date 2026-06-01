package cn.zswltech.mithras.service.others;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author junke
 */
@Component
public class SpringContextHolder implements ApplicationContextAware, EnvironmentAware {
    private static ApplicationContext applicationContext;
    private static Environment environment;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        assertApplicationContext();
        return applicationContext;
    }

    public static <T> T getBean(String beanName) {
        assertApplicationContext();
        return (T) applicationContext.getBean(beanName);
    }

    public static <T> T getBean(Class<T> requiredType) {
        assertApplicationContext();
        return applicationContext.getBean(requiredType);
    }

    private static void assertApplicationContext() {
        if (SpringContextHolder.applicationContext == null) {
            throw new RuntimeException("applicaitonContext属性为null,请检查是否注入了SpringContextHolder!");
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        SpringContextHolder.environment = environment;
    }

    public static String getProperty(String propertyName) {
        assertEnvironment();
        return environment.getProperty(propertyName);
    }

    public static <T> T getProperty(String propertyName, Class<T> type) {
        assertEnvironment();
        return environment.getProperty(propertyName, type);
    }

    private static void assertEnvironment() {
        if (SpringContextHolder.environment == null) {
            throw new RuntimeException("environment属性为null,请检查是否注入了SpringContextHolder!");
        }
    }
}