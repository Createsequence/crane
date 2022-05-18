package top.xiajibagao.crane.core.parser.interfaces;

import top.xiajibagao.crane.core.container.Container;
import top.xiajibagao.crane.core.operator.interfaces.Assembler;

import java.util.List;
import java.util.Set;

/**
 * 字段装配配置
 * <p>表明一次需要根据指定字段获取关联数据，并填充到当前对象的装配操作
 * 一个字段装配配置应当能够描述：
 * <ol>
 *     <li>以类中哪个字段为key?</li>
 *     <li>去哪个容器获取数据源?</li>
 *     <li>使用哪个装配器?</li>
 *     <li>将数据源中的哪些字段填到待处理对象中的哪些字段?</li>
 * </ol>
 *
 * @see OperationConfiguration
 * @see Assembler
 * @see Container
 * @see AssembleProperty
 * @author huangchengxing
 * @date 2022/03/01 14:55
 */
public interface AssembleOperation extends Operation {

    /**
     * 获取该操作所属的类操作配置实例
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
     * 获取装配容器
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

    /**
     * 获取分组
     *
     * @return java.util.Set<java.lang.Class<?>>
     * @author huangchengxing
     * @date 2022/5/18 20:47
     */
    Set<Class<?>> getGroups();

}
