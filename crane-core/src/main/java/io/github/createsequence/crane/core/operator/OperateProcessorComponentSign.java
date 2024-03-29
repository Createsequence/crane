package io.github.createsequence.crane.core.operator;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import io.github.createsequence.crane.core.annotation.ProcessorComponent;
import io.github.createsequence.crane.core.operator.interfaces.OperateProcessorComponent;
import lombok.Getter;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.util.Objects;
import java.util.Optional;

/**
 * {@link OperateProcessorComponent}的简单实现
 *
 * @author huangchengxing
 * @date 2022/06/28 15:00
 * @since 0.6.0
 */
@Getter
public class OperateProcessorComponentSign implements OperateProcessorComponent {

    private final String[] registerGroups;
    private final Class<?> targetClass;

    public OperateProcessorComponentSign(Class<?> targetClass, String... defaultRegisterGroups) {
        Assert.notNull(targetClass, "targetClass must not null");
        this.targetClass = targetClass;
        this.registerGroups = Optional.ofNullable(targetClass)
            .map(t -> AnnotatedElementUtils.findMergedAnnotation(t, ProcessorComponent.class))
            .map(ProcessorComponent::value)
            .orElse(defaultRegisterGroups);
        Assert.notNull(this.registerGroups, "defaultRegisterGroups must not null");
    }

    /**
     * 组件是否可以注册到当前组件中
     *
     * @param registrable 要注册的组件
     * @return boolean
     * @author huangchengxing
     * @date 2022/6/28 13:50
     */
    @Override
    public boolean isRegistrable(OperateProcessorComponent registrable) {
        return Objects.nonNull(registrable)
            && ArrayUtil.containsAny(getRegisterGroups(), registrable.getRegisterGroups());
    }

}
