package top.xiajibagao.crane.core.parser.interfaces;

import top.xiajibagao.crane.core.operator.interfaces.OperatorFactory;

/**
 * 类型操作配置解析器
 *
 * @param <T> 解析器解析处的配置类
 * @author huangchengxing
 * @date 2022/03/01 15:52
 */
public interface OperateConfigurationParser<T extends OperationConfiguration> {
    
    /**
     * 解析目标类配置
     *
     * @param targetClass 目标类型
     * @param operatorFactory 操作者工厂
     * @return top.xiajibagao.crane.parse.interfaces.OperationConfiguration
     * @author huangchengxing
     * @date 2022/3/1 15:54
     */
    T parse(Class<?> targetClass, OperatorFactory operatorFactory);

}
