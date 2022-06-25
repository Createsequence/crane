package top.xiajibagao.crane.core.helper.invoker;

import lombok.RequiredArgsConstructor;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 基于JDK反射调用的{@link MethodInvoker}实现
 *
 * @author huangchengxing
 * @date 2022/06/07 18:54
 * @since 0.5.5
 */
@RequiredArgsConstructor
public class ReflexMethodInvoker implements MethodInvoker {

    private final Method method;

    @Override
    public Object invoke(@Nonnull Object target, Object... args) {
        Objects.requireNonNull(target);
        return ReflectionUtils.invokeMethod(method, target, args);
    }

}
