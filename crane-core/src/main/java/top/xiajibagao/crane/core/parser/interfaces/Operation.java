package top.xiajibagao.crane.core.parser.interfaces;

import top.xiajibagao.crane.core.helper.Orderly;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * 字段操作，表明针对类中的某一字段进行的一项特定操作
 *
 * @author huangchengxing
 * @date 2022/04/14 13:31
 * @see DisassembleOperation
 * @see AssembleOperation
 */
public interface Operation extends Orderly {

    /**
     * 获取待处理的字段
     *
     * @return java.lang.reflect.Field
     * @author huangchengxing
     * @date 2022/3/1 15:03
     */
    Field getTargetProperty();

    /**
     * 获取待处理的字段的别名，该别名应当只能在无法获取{@link #getTargetProperty()}对应key值的情况下生效
     *
     * @return java.util.Set<java.lang.String>
     * @author huangchengxing
     * @date 2022/3/1 15:30
     */
    Set<String> getTargetPropertyAliases();

}
