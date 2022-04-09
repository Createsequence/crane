package top.xiajibagao.crane.core.helper;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 可序列化的{@link Function}接口
 *
 * @author huangchengxing
 * @date 2022/04/09 21:37
 */
@FunctionalInterface
public interface SFunc<P, R> extends Serializable, Function<P, R> {
}
