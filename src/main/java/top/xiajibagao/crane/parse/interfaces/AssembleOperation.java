package top.xiajibagao.crane.parse.interfaces;

import top.xiajibagao.crane.container.Container;
import top.xiajibagao.crane.operator.interfaces.Assembler;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

/**
 * 装配操作
 *
 * @author huangchengxing
 * @date 2022/03/01 14:55
 */
public interface AssembleOperation extends Comparable<AssembleOperation> {

    @Override
    default int compareTo(AssembleOperation o) {
        return Integer.compare(this.getSort(), o.getSort());
    }

    /**
     * 获取排序
     *
     * @return int
     * @author huangchengxing
     * @date 2022/3/5 11:53
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
