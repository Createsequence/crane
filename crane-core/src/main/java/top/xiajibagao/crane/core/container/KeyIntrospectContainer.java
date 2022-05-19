package top.xiajibagao.crane.core.container;

import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import top.xiajibagao.crane.core.helper.ObjectUtils;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;

/**
 * 内省容器，用于对待处理对象的 key 字段本身进行操作，可以理解其数据源对象就是“自己”
 *
 * @author huangchengxing
 * @date 2022/04/12 23:15
 */
@Slf4j
public class KeyIntrospectContainer implements Container {

    @Override
    public void process(Multimap<AssembleOperation, ?> operations) {
        operations.forEach((op, t) -> ObjectUtils.tryAction(
            () -> {
                Object key = op.getAssembler().getKey(t, op);
                ObjectUtils.acceptIfNotNull(key, k -> op.getAssembler().execute(t, k, op));
            },
            x -> log.error("字段[{}]处理失败，错误原因：{}", op.getTargetProperty(), x.getMessage())
        ));
    }

}
