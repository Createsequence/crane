package top.xiajibagao.crane.core.helper.invoker;

import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;

/**
 * @author huangchengxing
 * @date 2022/06/07 18:55
 */
@RequiredArgsConstructor
public class AsmReflexMethodInvoker implements MethodInvoker {

    private final MethodAccess methodAccess;
    private final int methodIndex;

    @Override
    public Object invoke(@Nonnull Object target, Object... args) {
        return methodAccess.invoke(target, methodIndex, args);
    }

}
