package top.xiajibagao.crane.container;

import top.xiajibagao.crane.operator.interfaces.Assembler;
import top.xiajibagao.crane.parse.interfaces.AssembleOperation;

import java.util.List;

/**
 * 容器，表明一类数据来源，{@link Assembler}可以从实现类中获取所需要的原始数据。容器中应当允许通过namespace进一步区分数据来源。
 *
 * @author huangchengxing
 * @date 2022/02/28 17:52
 */
public interface Container {

    /**
     * 根据指定配置处理数据
     *
     * @param targets 待处理数据集合
     * @param operations 待处理的装配操作
     * @author huangchengxing
     * @date 2022/2/23 20:49
     */
    void process(List<Object> targets, List<AssembleOperation> operations);

}
