package top.xiajibagao.crane.core.helper;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link BeanFactory}工具类
 *
 * @author huangchengxing
 * @date 2022/05/21 17:52
 */
public class BeanFactoryUtils {

    private BeanFactoryUtils() {
    }

    /**
     * 根据bean类型与名称获取bean，当bean名称为空时，将只根据类型获取
     *
     * @param beanFactory beanFactory
     * @param beanType bean类型
     * @param beanName bean名称
     * @return T
     * @throws NoSuchBeanDefinitionException 找不到对应的Bean时抛出
     * @author huangchengxing
     * @date 2022/5/21 17:56
     */
    @Nonnull
    public static <T> T getBean(@Nonnull BeanFactory beanFactory, @Nonnull Class<T> beanType, @Nullable String beanName) {
        T target = null;
        if (CharSequenceUtil.isBlank(beanName)) {
            target = beanFactory.getBean(beanType);
            Assert.notNull(target, () -> new NoSuchBeanDefinitionException(beanType));
            return target;
        }
        target = beanFactory.getBean(beanName, beanType);
        Assert.notNull(target, () -> new NoSuchBeanDefinitionException(
            CharSequenceUtil.format("not bean of type {} and named {} available", beanType, beanName)
        ));
        return target;
    }

}
