package top.xiajibagao.crane.core.parser.interfaces;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * 表示针对某个字段的一次操作
 *
 * @author huangchengxing
 * @date 2022/04/14 13:31
 */
public interface Operation extends Comparable<Operation> {

    @Override
    default int compareTo(Operation o) {
        return Integer.compare(this.getSort(), o.getSort());
    }

    /**
     * 获取排序
     *
     * @return int
     * @author huangchengxing
     * @date 2022/3/5 11:46
     */
    int getSort();

    /**
     * 获取注解字段
     *
     * @return java.lang.reflect.Field
     * @author huangchengxing
     * @date 2022/3/1 15:03
     */
    Field getTargetProperty();

    /**
     * 获取注解字段别名
     *
     * @return java.util.Set<java.lang.String>
     * @author huangchengxing
     * @date 2022/3/1 15:30
     */
    Set<String> getTargetPropertyAliases();

}
