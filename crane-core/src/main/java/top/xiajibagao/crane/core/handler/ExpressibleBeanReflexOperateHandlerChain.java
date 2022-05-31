package top.xiajibagao.crane.core.handler;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.Getter;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandlerChain;
import top.xiajibagao.crane.core.helper.ExpressionUtils;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.AssembleProperty;

import java.util.function.Supplier;

/**
 * SpEL表达式装配处理器链 <br />
 * 该类可视为{@link BeanReflexOperateHandlerChain}的代理类，在被代理的原处理器链的基础上，当向待处理对象写入数据时，
 * 该处理器将拦截数据并执行表达式获取返回值作为新的数据源，并再次调用处理器链尝试写入该值。
 * 此外，其他行为皆与原处理器链保持一致。
 *
 * @since 0.5.3
 * @author huangchengxing
 * @date 2022/04/13 0:06
 */
@Getter
public class ExpressibleBeanReflexOperateHandlerChain extends BeanReflexOperateHandlerChain implements OperateHandlerChain {

    private final Supplier<StandardEvaluationContext> contextFactory;

    public ExpressibleBeanReflexOperateHandlerChain(Supplier<StandardEvaluationContext> contextFactory) {
        this.contextFactory = contextFactory;
    }

    @Override
    public void writeToTarget(Object sourceData, Object target, AssembleProperty property, AssembleOperation operation) {
        if (CharSequenceUtil.isBlank(property.getExp())) {
            super.writeToTarget(sourceData, target, property, operation);
            return;
        }
        StandardEvaluationContext context = contextFactory.get();
        context.setVariable("target", target);
        context.setVariable("source", sourceData);
        context.setVariable("key", operation.getAssembler().getKey(target, operation));
        context.setVariable("src", property.getResource());
        context.setVariable("ref", property.getReference());
        sourceData = ExpressionUtils.execute(property.getExp(), context, property.getExpType(), true);
        super.writeToTarget(sourceData, target, property, operation);
    }
}
