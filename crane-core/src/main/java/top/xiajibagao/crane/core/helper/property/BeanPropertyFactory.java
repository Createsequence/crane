package top.xiajibagao.crane.core.helper.property;


import java.util.Optional;

/**
 * {@link BeanProperty}工厂
 *
 * @see AbstractBeanPropertyFactory
 * @see AsmReflexBeanPropertyFactory
 * @see ReflexBeanPropertyFactory
 * @author huangchengxing
 * @date 2022/05/10 15:51
 */
public interface BeanPropertyFactory {

    /**
     * 获取类属性对应的{@link BeanProperty}实例
     *
     * @param targetClass 类
     * @param fieldName 属性名称
     * @return java.util.Optional<top.xiajibagao.crane.core.helper.property.BeanProperty>
     * @author huangchengxing
     * @date 2022/6/2 8:23
     * @since 0.5.4
     */
    Optional<BeanProperty> getProperty(Class<?> targetClass, String fieldName);

}
