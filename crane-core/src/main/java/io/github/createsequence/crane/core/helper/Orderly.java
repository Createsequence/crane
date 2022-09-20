package io.github.createsequence.crane.core.helper;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.annotation.OrderUtils;

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
 * @author huangchengxing
 * @date 2022/04/15 13:06
 * @see Ordered
 * @see Comparable
 * @see OrderUtils
 */
public interface Orderly extends Ordered, Comparable<Orderly> {

    /**
     * 用于比较{@link Orderly}实现类的比较器，可通过{@link #comparator()}获取
     *
     * @since 0.5.8
     */
    Comparator<Orderly> ORDERLY_COMPARATOR = Comparator.comparingInt(Orderly::getActualOrder);

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
     * 获取真实排序值。<br />
     * 当实现类上存在{@link Order}注解时，返回{@link Order#value()}，否则返回{@link #getOrder()};
     *
     * @see OrderUtils#getOrder(Class, int)
     * @return int
     * @author huangchengxing
     * @date 2022/6/8 11:37
     * @see 0.5.6
     */
    default int getActualOrder() {
        return OrderUtils.getOrder(this.getClass(), getOrder());
    }

    /**
     * 获取根据{@link #getOrder()}的返回值比较两个实例的大小
     *
     * @param o 比较对象
     * @return java.util.Comparator<helper.io.github.createsequence.crane.core.Orderly>
     * @author huangchengxing
     * @date 2022/4/15 13:08
     */
    @Override
    default int compareTo(@Nonnull Orderly o) {
        return Objects.compare(this, o, comparator());
    }

    /**
     * 获取按排序值比较器。<br />
     * 当比较时，若存在类存在{@link Order}注解，则优先从注解获取排序值，否则通过{@link #getOrder()}获取排序值。
     *
     * @see OrderUtils#getOrder(Class, int)
     * @return java.util.Comparator<helper.io.github.createsequence.crane.core.Orderly>
     * @author huangchengxing
     * @date 2022/4/15 13:08
     */
    static Comparator<Orderly> comparator() {
        return Orderly.ORDERLY_COMPARATOR;
    }

}
