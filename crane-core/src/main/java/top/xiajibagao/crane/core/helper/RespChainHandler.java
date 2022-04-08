package top.xiajibagao.crane.core.helper;

/**
 * 抽象的责任链执行节点，将操作对象作为上下文，针对该对象的操作抽象为多个责任链节点
 *
 * @param <C> 上下文类型
 * @author huangchengxing
 * @date 2022/04/06 12:56
 */
public interface RespChainHandler<C> {

    /**
     * 节点执行顺序，越小越靠前
     *
     * @return int
     * @author huangchengxing
     * @date 2022/4/6 12:58
     */
    default int order() {
        return Integer.MIN_VALUE;
    }

    /**
     * 是否执行当前节点
     *
     * @param context 上下文
     * @return boolean
     * @author huangchengxing
     * @date 2022/4/6 12:58
     */
    default boolean support(C context) {
        return true;
    }

    /**
     * 处理上下文
     *
     * @param context 上下文
     * @return boolean 是否继续执行后续节点
     * @author huangchengxing
     * @date 2022/4/6 12:57
     */
    boolean process(C context);

}
