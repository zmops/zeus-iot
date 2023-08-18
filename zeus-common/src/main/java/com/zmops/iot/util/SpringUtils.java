package com.zmops.iot.util;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * @Author nantian
 * @Date 2/20/2020 0020 11:09 AM
 * @Email nantian@zmops.com
 * @Version 1.0
 */
@Component
public class SpringUtils implements BeanFactoryPostProcessor {

    private static ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringUtils.beanFactory = beanFactory;
    }

    /**
     * 获取对象
     */
    public static Object getBean(String name) {
        return beanFactory.getBean(name);
    }

    /**
     * 获取对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name, Class<T> clz) {
        T result = (T) beanFactory.getBean(name);
        return result;
    }

    /**
     * 获取对象
     */
    public static <T> T getBean(Class<T> clz) {
        T result = (T) beanFactory.getBean(clz);
        return result;
    }

    /**
     * 判断是否包含对象
     */
    public static boolean containsBean(String name) {
        return beanFactory.containsBean(name);
    }

    /**
     * 创建对象到spring context
     */
    @SuppressWarnings("unchecked")
    public static <T> T createBean(Class<T> clz) {
        T result = (T) beanFactory.createBean(clz, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, true);
        return result;
    }

    /**
     * 删除对象
     */
    public static void destroyBean(String beanName) {
        beanFactory.destroyScopedBean(beanName);
    }
}
