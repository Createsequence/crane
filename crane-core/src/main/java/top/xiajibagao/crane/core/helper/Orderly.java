package top.xiajibagao.crane.core.helper;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Objects;

/**
 * 表明实现类允许根据{@link Order}注解，或实现类重写的{@link #getOrder()}排序
 *
 * <p>表明实现类之间允许根据优先级排序，{@link #getOrder()}返回值越小，则优先级越高，排序时越靠前。
 * 实现此接口暗示实现类是允许排序的，但是否真的可以排序则需要实现者提供相关支持。
 * 当实现类指定了顺序，但是与{@link Order}注解不一致时，应当以注解为准。
 *
 * @see Ordered
 * @see Comparable
 * @author huangchengxing
 * @date 2022/04/15 13:06
 */
public interface Orderly extends Ordered, Comparable<Orderly> {

    /**
     * 排序值，值越小则优先级越高，排序越靠前 <br />
     * 一般情况下，实例若存在{@link Order}注解，则其对应的方法返回值应当与注解值保持一致,
     * 若不一致时，应当以Order为准。
     *
     * @return int
     * @author huangchengxing
     * @date 2022/4/8 20:30
     */
    @Override
    default int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    /**
     * 获取根据{@link #getOrder()}的返回值比较两个实例的大小
     *
     * @param o 比较对象
     * @return java.util.Comparator<top.xiajibagao.crane.core.helper.Orderly>
     * @author huangchengxing
     * @date 2022/4/15 13:08
     */
    @Override
    default int compareTo(@Nonnull Orderly o) {
        return Objects.compare(this, o, comparator());
    }

    /**
     * 获取根据{@link #getOrder()}返回值比较的比较器，默认返回值越小优先级越大
     *
     * @return java.util.Comparator<top.xiajibagao.crane.core.helper.Orderly>
     * @author huangchengxing
     * @date 2022/4/15 13:08
     */
    static Comparator<Orderly> comparator() {
        return Comparator.comparingInt(Orderly::getOrder);
    }

}
