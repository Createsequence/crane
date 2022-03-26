package top.xiajibagao.crane.extend;

import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.helper.CollUtils;
import top.xiajibagao.crane.operator.interfaces.OperationExecutor;
import top.xiajibagao.crane.operator.interfaces.OperatorFactory;
import top.xiajibagao.crane.parse.interfaces.OperateConfigurationParser;
import top.xiajibagao.crane.parse.interfaces.OperationConfiguration;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * {@link ProcessTemplate}的基础实现
 *
 * @author huangchengxing
 * @date 2022/03/26 13:32
 */
@RequiredArgsConstructor
public class BeanProcessTemplate implements ProcessTemplate {

    private final OperateConfigurationParser<? extends OperationConfiguration> configurationParser;
    private final OperationExecutor defaultOperationExecutor;
    private final OperatorFactory defaultOperatorFactory;

    @Override
    public void process(Collection<?> targets, OperatorFactory operatorFactory, OperationExecutor executor) {
        if (CollUtils.isEmpty(targets)) {
            return;
        }
        Optional<Class<?>> targetClass = targets.stream()
            .filter(Objects::nonNull)
            .findFirst()
            .map(Object::getClass);
        targetClass.ifPresent(t -> {
            OperationConfiguration configuration = configurationParser.parse(t, operatorFactory);
            executor.execute(targets, configuration);
        });
    }

    @Override
    public void process(Collection<?> targets) {
        process(targets, defaultOperatorFactory, defaultOperationExecutor);
    }

}
