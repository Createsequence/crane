package top.xiajibagao.crane.core.helper.invoker;

import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;

/**
 * 基于字节码调用的{@link MethodInvoker}实现
 *
 * @author huangchengxing
 * @date 2022/06/07 18:55
 * @since 0.5.5
 */
@RequiredArgsConstructor
public class AsmReflexMethodInvoker implements MethodInvoker {

    private final MethodAccess methodAccess;
    private final int methodIndex;

    @Override
    public Object invoke(@Nullable Object target, @Nullable Object... args) {
        return methodAccess.invoke(target, methodIndex, args);
    }

}
