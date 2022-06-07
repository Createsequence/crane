package top.xiajibagao.crane.core.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author huangchengxing
 * @date 2022/06/07 18:54
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
