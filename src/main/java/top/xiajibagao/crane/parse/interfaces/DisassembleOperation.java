package top.xiajibagao.crane.parse.interfaces;

import top.xiajibagao.crane.operator.interfaces.Disassembler;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * 拆卸操作
 *
 * @author huangchengxing
 * @date 2022/03/01 14:55
 */
public interface DisassembleOperation extends Comparable<DisassembleOperation> {
    
    /**
     * 比较两者{@link #getSort()}排序的大小
     *
     * @param o 另一操作配置
     * @return int
     * @author huangchengxing
     * @date 2022/3/5 11:50
     */
    @Override
    default int compareTo(DisassembleOperation o) {
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
     * 获取所属类操作
     *
     * @return top.xiajibagao.crane.parse.interfaces.OperationConfiguration
     * @author huangchengxing
     * @date 2022/3/1 15:34
     */
    OperationConfiguration getOwner();

    /**
     * 获取拆卸器
     *
     * @return top.xiajibagao.crane.operator.interfaces.Disassembler
     * @author huangchengxing
     * @date 2022/3/1 15:57
     */
    Disassembler getDisassembler();

    /**
     * 待拆解对象的操作配置
     *
     * @return top.xiajibagao.crane.parse.interfaces.OperationConfiguration
     * @author huangchengxing
     * @date 2022/3/1 15:35
     */
    OperationConfiguration getTargetOperateConfiguration();

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
