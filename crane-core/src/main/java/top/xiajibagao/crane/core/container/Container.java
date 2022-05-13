package top.xiajibagao.crane.core.container;

import com.google.common.collect.Multimap;
import top.xiajibagao.crane.core.helper.Orderly;
import top.xiajibagao.crane.core.operator.interfaces.Assembler;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.Operation;

/**
 * 装配容器
 *
 * <p>根据一组{@link AssembleOperation}配置完成对一批对象的装配处理的“车间”。
 * 容器中一般用以完成以下步骤：
 * <ol>
 *     <li>使用{@link Assembler}从待处理对象中获取对应key值；</li>
 *     <li>根据操作配置指定的namespace(如果有)和key值获取对应数据源对象；</li>
 *     <li>再次使用{@link Assembler}将数据源根据指定配置写入待处理对象中；</li>
 * </ol>
 *
 * <p>该接口默认提供基于key查询的初步实现{@link BaseKeyContainer}，
 * 与同时基于key与namespace的初步实现{@link BaseNamespaceContainer}。
 *
 * <p>容器之间支持根据排序调整其接受{@link Operation}的顺序，但是仍然应当以保证{@link Operation}的顺序优先。
 *
 * @see AssembleOperation
 * @see BaseKeyContainer
 * @see BaseNamespaceContainer
 * @author huangchengxing
 * @date 2022/02/28 17:52
 */
public interface Container extends Orderly {

    /**
     * 根据指定装配操作配置处理待处理对象
     *
     * @param operations 待处理对象与待处理的装配操作配置
     * @author huangchengxing
     * @date 2022/2/23 20:49
     */
    void process(Multimap<AssembleOperation, ?> operations);

}
