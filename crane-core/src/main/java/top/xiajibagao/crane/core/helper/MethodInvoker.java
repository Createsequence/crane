package top.xiajibagao.crane.core.helper;

import javax.annotation.Nonnull;

/**
 * 描述一个可以被调用的方法
 *
 * @see ParamConvertMethodInvoker
 * @author huangchengxing
 * @date 2022/06/07 17:48
 * @since 0.5.5
 */
@FunctionalInterface
public interface MethodInvoker {

    /**
     * 调用方法
     *
     * @param target 被调用的对象
     * @param args 参数
     * @return java.lang.Object
     * @author huangchengxing
     * @date 2022/6/7 17:49
     */
    Object invoke(@Nonnull Object target, Object... args);

}
