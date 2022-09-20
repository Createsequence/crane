package io.github.createsequence.crane.core.parser.interfaces;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.createsequence.crane.core.operator.interfaces.Disassembler;

import java.util.Collection;
import java.util.Objects;

/**
 * 字段装卸配置
 *
 * <p>表明一次需要从某个字段中获取数据装卸操作。一个字段装卸配置应当能够描述：
 * <ol>
 *     <li>装卸类中哪一个字段?</li>
 *     <li>使用哪个装卸器?</li>
 *     <li>将获取的字段值拆分为哪些待处理对象?</li>
 * </ol>
 *
 * <p>存在不确定类型的装卸操作实现{@link DynamicDisassembleOperation}，
 * 可以使用{@link #isDynamic(DisassembleOperation)}确定是该操作否为一个
 * 不确定类型的装卸操作。
 *
 * @author huangchengxing
 * @date 2022/03/01 14:55
 * @see DynamicDisassembleOperation
 * @see OperationConfiguration
 * @see Disassembler
 */
public interface DisassembleOperation extends Operation {
    
    /**
     * 获取该操作所属的类操作配置实例
     *
     * @return io.github.createsequence.crane.parse.interfaces.OperationConfiguration
     * @author huangchengxing
     * @date 2022/3/1 15:34
     */
    OperationConfiguration getOwner();

    /**
     * 获取装卸器
     *
     * @return io.github.createsequence.crane.operator.interfaces.Disassembler
     * @author huangchengxing
     * @date 2022/3/1 15:57
     */
    Disassembler getDisassembler();

    /**
     * 获取装卸后获取的待处理对象对应的操作配置
     *
     * @return io.github.createsequence.crane.parse.interfaces.OperationConfiguration
     * @author huangchengxing
     * @date 2022/3/1 15:35
     */
    OperationConfiguration getTargetOperateConfiguration();
    
    /**
     * 当前操作是否是动态装卸操作
     *
     * @param operation 操作
     * @return boolean
     * @author huangchengxing
     * @date 2022/6/24 13:15
     */
    static boolean isDynamic(DisassembleOperation operation) {
        return operation instanceof DynamicDisassembleOperation;
    }

    /**
     * 根据装卸操作配置，从一组待处理的嵌套对象获取装卸字段值，然后获取值对象与对应的操作配置
     *
     * @param disassembleOperation 装卸操作
     * @param targets 待处理对象
     * @return com.google.common.collect.Multimap<interfaces.parser.io.github.createsequence.crane.core.OperationConfiguration,java.lang.Object>
     * @author huangchengxing
     * @date 2022/6/24 13:51
     */
    static Multimap<OperationConfiguration, Object> collect(DisassembleOperation disassembleOperation, Collection<?> targets) {
        boolean isDynamic = isDynamic(disassembleOperation);
        Multimap<OperationConfiguration, Object> operationConfigurations = ArrayListMultimap.create();
        for (Object target : targets) {
            DisassembleOperation operation = isDynamic ?
                ((DynamicDisassembleOperation) disassembleOperation).resolve(target) : disassembleOperation;
            if (Objects.isNull(operation)) {
                continue;
            }
            Collection<?> values = operation.getDisassembler().execute(target, operation);
            if (CollUtil.isNotEmpty(values)) {
                operationConfigurations.putAll(operation.getTargetOperateConfiguration(), values);
            }
        }
        return operationConfigurations;
    }

}
