package top.xiajibagao.crane.core.helper.invoker;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * 描述一个可以被调用的方法，当方法实际入参与规定的参数类型不一致时，将会自动进行类型转换
 *
 * @see Convert
 * @see MethodInvoker
 * @author huangchengxing
 * @date 2022/06/07 17:51
 * @since 0.5.5
 */
@RequiredArgsConstructor
public class ParamTypeAutoConvertInvoker implements MethodInvoker {

    private final Class<?>[] paramTypes;
    private final MethodInvoker method;

    @Override
    public Object invoke(@Nonnull Object target, Object... args) {
        if (Objects.nonNull(args)) {
            args = (args.length == paramTypes.length) ? args : ArrayUtil.resize(args, paramTypes.length);
            for (int i = 0; i < paramTypes.length; i++) {
                Object arg = args[i];
                Class<?> methodArgType = paramTypes[i];
                args[i] = Convert.convert(methodArgType, arg);
            }
        }
        return method.invoke(target, args);
    }

}
