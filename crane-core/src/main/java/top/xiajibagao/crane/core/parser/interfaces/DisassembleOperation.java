package top.xiajibagao.crane.core.parser.interfaces;

import top.xiajibagao.crane.core.operator.interfaces.Disassembler;

/**
 * 字段装卸配置
 * <p>表明一次需要从某个字段中获取数据装卸操作
 * 一个字段装卸配置应当能够描述：
 * <ol>
 *     <li>装卸类中哪一个字段?</li>
 *     <li>使用哪个装卸器?</li>
 *     <li>将获取的字段值拆分为哪些待处理对象?</li>
 * </ol>
 *
 * @see OperationConfiguration
 * @see Disassembler
 * @author huangchengxing
 * @date 2022/03/01 14:55
 */
public interface DisassembleOperation extends Operation {
    
    /**
     * 获取该操作所属的类操作配置实例
     *
     * @return top.xiajibagao.crane.parse.interfaces.OperationConfiguration
     * @author huangchengxing
     * @date 2022/3/1 15:34
     */
    OperationConfiguration getOwner();

    /**
     * 获取装卸器
     *
     * @return top.xiajibagao.crane.operator.interfaces.Disassembler
     * @author huangchengxing
     * @date 2022/3/1 15:57
     */
    Disassembler getDisassembler();

    /**
     * 获取装卸后获取的待处理对象对应的操作配置
     *
     * @return top.xiajibagao.crane.parse.interfaces.OperationConfiguration
     * @author huangchengxing
     * @date 2022/3/1 15:35
     */
    OperationConfiguration getTargetOperateConfiguration();

}
