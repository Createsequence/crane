package top.xiajibagao.crane.extension.container;

import lombok.extern.slf4j.Slf4j;
import top.xiajibagao.crane.core.container.Container;
import top.xiajibagao.crane.core.helper.CollUtils;
import top.xiajibagao.crane.core.helper.ObjectUtils;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;

import java.util.List;

/**
 * 内省容器，用于对待处理对象本身进行操作，可以理解其数据源对象就是“自己”
 *
 * @author huangchengxing
 * @date 2022/04/12 23:15
 */
@Slf4j
public class IntrospectContainer implements Container {

    @Override
    public void process(List<Object> targets, List<AssembleOperation> operations) {
        CollUtils.biForEach(targets, operations, (target, operation) -> ObjectUtils.tryAction(
            () -> operation.getAssembler().execute(target, target, operation),
            x -> log.error("字段[{}]处理失败，错误原因：{}", operation.getTargetProperty(), x.getMessage())
        ));
    }

}
