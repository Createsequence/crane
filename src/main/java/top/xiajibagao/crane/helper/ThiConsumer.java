package top.xiajibagao.crane.helper;

/**
 * @author huangchengxing
 * @date 2022/04/01 13:31
 */
@FunctionalInterface
public interface ThiConsumer<A, B, C> {
    void accept(A a, B b, C c);
}
