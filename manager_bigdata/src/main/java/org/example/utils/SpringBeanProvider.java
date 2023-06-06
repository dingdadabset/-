package org.example.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringBeanProvider implements ApplicationContextAware { //继承上下文工具组件，用于获得上下文容器

        private static ApplicationContext context;

        private SpringBeanProvider(){}

        //获取上下文对象，用于从容器中提取组件对象
        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            context = applicationContext;
        }

        //从名称获取容器中的组件
        public  static <T> T getBean(String name,Class<T> aClass){
            return context.getBean(name,aClass);
        }

}
