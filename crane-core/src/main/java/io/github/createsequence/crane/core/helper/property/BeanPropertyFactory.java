package io.github.createsequence.crane.core.helper.property;


import java.util.Optional;

/**
 * {@link BeanProperty}工厂
 *
 * @author huangchengxing
 * @date 2022/05/10 15:51
 * @see AbstractBeanPropertyFactory
 * @see AsmReflexBeanPropertyFactory
 * @see ReflexBeanPropertyFactory
 */
public interface BeanPropertyFactory {

    /**
     * 获取类属性对应的{@link BeanProperty}实例
     *
     * @param targetClass 类
     * @param fieldName 属性名称
     * @return java.util.Optional<property.helper.io.github.createsequence.crane.core.BeanProperty>
     * @author huangchengxing
     * @date 2022/6/2 8:23
     * @since 0.5.4
     */
    Optional<BeanProperty> getProperty(Class<?> targetClass, String fieldName);

}
