package top.xiajibagao.crane.core.operator.interfaces;

import top.xiajibagao.crane.core.parser.interfaces.DisassembleOperation;

import java.util.Collection;

/**
 * 装卸器
 * <p>用于将对象中带有嵌套结构的字段值取出，并平摊处理为复数需要进行装配操作的待处理对象
 *
 * @author huangchengxing
 * @date 2022/03/01 14:45
 */
public interface Disassembler extends Operator {

    /**
     * 根据装卸操作配置，将待处理对象中指定字段的值拆分平铺为需要进行装配的复数待处理对象
     *
     * @param target 待处理对象
     * @param operation 装卸操作配置
     * @return java.util.Collection<?>
     * @author huangchengxing
     * @date 2022/3/1 14:40
     */
    Collection<?> execute(Object target, DisassembleOperation operation);

}
