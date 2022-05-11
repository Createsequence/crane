package top.xiajibagao.crane.core.helper.reflex;

import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author huangchengxing
 * @date 2022/05/11 11:42
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class IndexedMethod {
    private final MethodAccess methodAccess;
    private final int methodIndex;

    public Object invoke(Object target, Object... args) {
        return methodAccess.invoke(target, methodIndex, args);
    }
}
