package top.xiajibagao.crane.operator.interfaces;

import top.xiajibagao.crane.parse.interfaces.DisassembleOperation;

import java.util.Collection;

/**
 * 拆卸器，用于将对象中带有嵌套结构的属性值取出处理
 *
 * @author huangchengxing
 * @date 2022/03/01 14:45
 */
public interface Disassembler extends Operator {

    /**
     * 根据配置完成卸载操作
     *
     * @param target 目标实例
     * @param operation 操作配置
     * @return java.util.Collection<?>
     * @author huangchengxing
     * @date 2022/3/1 14:40
     */
    Collection<?> execute(Object target, DisassembleOperation operation);

}
