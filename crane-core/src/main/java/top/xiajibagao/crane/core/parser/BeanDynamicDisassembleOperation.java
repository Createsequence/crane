package top.xiajibagao.crane.core.parser;

import cn.hutool.core.collection.CollUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.core.cache.ConfigurationCache;
import top.xiajibagao.crane.core.operator.interfaces.Disassembler;
import top.xiajibagao.crane.core.parser.interfaces.DynamicDisassembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.OperateConfigurationParser;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Deque;
import java.util.Objects;
import java.util.Set;

/**
 * @author huangchengxing
 * @date 2022/06/24 11:33
 */
@RequiredArgsConstructor
@Getter
public class BeanDynamicDisassembleOperation implements DynamicDisassembleOperation {

    private final OperateConfigurationParser configurationParser;
    private final int order;
    private final OperationConfiguration owner;
    private final Disassembler disassembler;
    private final Field targetProperty;
    private final Set<String> targetPropertyAliases;
    private final ConfigurationCache configurationCache;

    @Nullable
    @Override
    public OperationConfiguration getTargetOperateConfiguration(Object target) {
        Class<?> targetClass = getTargetClass(target);
        return configurationCache.getOrCached(
            configurationParser.getClass().getName(),
            targetClass,
            configurationParser::parse
        );
    }

    @Nullable
    private Class<?> getTargetClass(Object target) {
        if (Objects.isNull(target)) {
            return null;
        }
        Class<?> targetClass = null;
        Deque<Object> deque = CollUtil.newLinkedList(target);
        while (!deque.isEmpty()) {
            Object targetObj = deque.removeFirst();
            if (Objects.isNull(targetObj)) {
                continue;
            }
            // 若是collection集合或者数组则继续遍历
            targetClass = targetObj.getClass();
            if ((targetObj instanceof Collection) || targetClass.isArray()) {
                CollUtil.addAll(deque, targetObj);
                continue;
            }
            // 若是对象，则直接取该对象类型作为目标类型
            break;
        }
        return targetClass;
    }
}
