package top.xiajibagao.crane.core.parser.interfaces;

import top.xiajibagao.crane.core.helper.Orderly;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * 表示针对某个字段的一次操作
 *
 * @author huangchengxing
 * @date 2022/04/14 13:31
 */
public interface Operation extends Orderly {

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
