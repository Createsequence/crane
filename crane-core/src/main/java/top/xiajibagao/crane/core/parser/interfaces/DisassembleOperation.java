package top.xiajibagao.crane.core.parser.interfaces;

import top.xiajibagao.crane.core.operator.interfaces.Disassembler;

/**
 * 拆卸操作
 *
 * @author huangchengxing
 * @date 2022/03/01 14:55
 */
public interface DisassembleOperation extends Operation {
    
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

}
