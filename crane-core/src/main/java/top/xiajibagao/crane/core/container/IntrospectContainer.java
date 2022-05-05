package top.xiajibagao.crane.core.container;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import top.xiajibagao.crane.core.container.Container;
import top.xiajibagao.crane.core.helper.CollUtils;
import top.xiajibagao.crane.core.helper.ObjectUtils;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;

/**
 * 内省容器，用于对待处理对象本身进行操作，可以理解其数据源对象就是“自己”
 *
 * @author huangchengxing
 * @date 2022/04/12 23:15
 */
@Slf4j
public class IntrospectContainer implements Container {

    @Override
    public void process(MultiValueMap<AssembleOperation, ?> operations) {
        CollUtils.forEach(operations, (op, t) -> ObjectUtils.tryAction(
            () -> op.getAssembler().execute(t, t, op),
            x -> log.error("字段[{}]处理失败，错误原因：{}", op.getTargetProperty(), x.getMessage())
        ));
    }
}
