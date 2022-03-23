package top.xiajibagao.crane.config;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.xiajibagao.crane.impl.bean.aop.MethodResultProcessAspect;

/**
 * @author huangchengxing
 * @date 2022/03/23 23:15
 */
@Configuration
public class TestExtendFuncConfig {

    @Bean("CraneDefaultMethodResultProcessAspect")
    public MethodResultProcessAspect methodResultProcessAspect(BeanFactory beanFactory) {
        return new MethodResultProcessAspect(beanFactory);
    }

}
