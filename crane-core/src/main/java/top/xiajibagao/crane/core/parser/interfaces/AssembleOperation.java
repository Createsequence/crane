package top.xiajibagao.crane.core.parser.interfaces;

import top.xiajibagao.crane.core.container.Container;
import top.xiajibagao.crane.core.operator.interfaces.Assembler;

import java.util.List;

/**
 * 装配操作
 *
 * @author huangchengxing
 * @date 2022/03/01 14:55
 */
public interface AssembleOperation extends Operation {

    /**
     * 获取所属类操作
     *
     * @return top.xiajibagao.crane.parse.interfaces.OperationConfiguration
     * @author huangchengxing
     * @date 2022/3/1 15:34
     */
    OperationConfiguration getOwner();

    /**
     * 获取命名空间
     *
     * @return java.lang.String
     * @author huangchengxing
     * @date 2022/3/1 15:31
     */
    String getNamespace();

    /**
     * 获取装配器
     *
     * @return top.xiajibagao.crane.operator.interfaces.Assembler
     * @author huangchengxing
     * @date 2022/3/1 15:57
     */
    Assembler getAssembler();

    /**
     * 获取数据源容器
     *
     * @return top.xiajibagao.crane.container.Container
     * @author huangchengxing
     * @date 2022/3/1 15:56
     */
    Container getContainer();

    /**
     * 获取装配配置字段
     *
     * @return java.util.List<top.xiajibagao.crane.parse.interfaces.AssembleProperty>
     * @author huangchengxing
     * @date 2022/3/1 16:02
     */
    List<AssembleProperty> getProperties();

}
