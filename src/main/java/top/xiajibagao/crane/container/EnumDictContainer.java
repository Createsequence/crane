package top.xiajibagao.crane.container;

import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.helper.EnumDict;
import top.xiajibagao.crane.parse.interfaces.AssembleOperation;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 通过命名空间（枚举名称）与枚举获取唯一值的{@link Container}实现
 *
 * @author huangchengxing
 * @date 2022/03/02 13:20
 */
@RequiredArgsConstructor
public class EnumDictContainer implements Container {

    private final EnumDict enumDict;

    public <T extends Enum<?>> void register(Class<T> targetClass, String typeName, Function<T, String> itemNameGetter) {
        enumDict.register(targetClass, typeName, itemNameGetter);
    }

    public void register(Class<? extends Enum<?>> targetClass) {
        enumDict.register(targetClass);
    }

    @Override
    public void process(List<Object> targets, List<AssembleOperation> operations) {
        for (AssembleOperation operation : operations) {
            targets.forEach(target -> {
                Object key = operation.getAssembler().getKey(target, operation);
                if (Objects.isNull(key)) {
                    return;
                }
                EnumDict.EnumDictItem<?> val = enumDict.getItem(operation.getNamespace(), key.toString());
                if (Objects.nonNull(val)) {
                    operation.getAssembler().execute(target, val.getBeanMap(), operation);
                }
            });
        }
    }

}
