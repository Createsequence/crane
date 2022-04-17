package top.xiajibagao.crane.core.parser.interfaces;

import top.xiajibagao.crane.core.operator.interfaces.Assembler;
import top.xiajibagao.crane.core.operator.interfaces.Disassembler;
import top.xiajibagao.crane.core.operator.interfaces.OperatorFactory;

/**
 * 操作配置解析器
 * <p>指定操作者工厂，然后将指定类型中的各项字段的装配与装卸操作配置解析并整合为操作配置
 *
 * @see OperationConfiguration
 * @see AssembleOperation
 * @see DisassembleOperation
 * @see OperatorFactory
 * @see Assembler
 * @see Disassembler
 * @param <T> 解析器解析处的配置类
 * @author huangchengxing
 * @date 2022/03/01 15:52
 */
public interface OperateConfigurationParser<T extends OperationConfiguration> {
    
    /**
     * 解析目标类型，获取该类型对应的类操作配置实例
     *
     * @param targetClass 目标类型
     * @param operatorFactory 操作者工厂
     * @return top.xiajibagao.crane.parse.interfaces.OperationConfiguration
     * @author huangchengxing
     * @date 2022/3/1 15:54
     */
    T parse(Class<?> targetClass, OperatorFactory operatorFactory);

}
