package io.github.createsequence.crane.core.helper.property;

import java.lang.reflect.Field;

/**
 * 描述一个类属性，提供针对该类型实例的此属性的get/set能力
 *
 * @author huangchengxing
 * @date 2022/04/01 14:08
 * @see BeanPropertyFactory
 * @see AsmReflexBeanPropertyFactory.AsmReflexBeanProperty
 * @see ReflexBeanPropertyFactory.ReflexBeanProperty
 */
public interface BeanProperty {

    /**
     * 获取所属类型
     *
     * @return java.lang.Class<?>
     * @author huangchengxing
     * @date 2022/5/9 16:38
     */
    Class<?> targetClass();

    /**
     * 获取属性
     *
     * @return java.lang.reflect.Field
     * @author huangchengxing
     * @date 2022/5/9 16:39
     */
    Field field();

    /**
     * 获取属性值
     *
     * @param target 目标对象
     * @return java.lang.Object
     * @author huangchengxing
     * @date 2022/5/9 16:39
     */
    Object getValue(Object target);

    /**
     * 设置属性值
     *
     * @param target 目标对象
     * @param value 属性值
     * @author huangchengxing
     * @date 2022/5/9 16:40
     */
    void setValue(Object target, Object value);

}
