package top.xiajibagao.crane.core.handler;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandlerChain;
import top.xiajibagao.crane.core.helper.ExpressionUtils;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.AssembleProperty;

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
@RequiredArgsConstructor
@Setter
public class ExpressibleBeanReflexOperateHandlerChain extends BeanReflexOperateHandlerChain implements OperateHandlerChain {

    private final ContextFactory contextFactory;

    @Override
    public void writeToTarget(Object sourceData, Object target, AssembleProperty property, AssembleOperation operation) {
        if (CharSequenceUtil.isBlank(property.getExp())) {
            super.writeToTarget(sourceData, target, property, operation);
            return;
        }
        StandardEvaluationContext context = contextFactory.get(new ProcessContext(sourceData, target, property, operation));
        sourceData = ExpressionUtils.execute(property.getExp(), context, property.getExpType(), true);
        super.writeToTarget(sourceData, target, property, operation);
    }

    @Getter
    @RequiredArgsConstructor
    public static class ProcessContext {
        private final Object sourceData;
        private final Object target;
        private final AssembleProperty property;
        private final AssembleOperation operation;
    }

    @FunctionalInterface
    public interface ContextFactory {

        /**
         * 根据本次执行的待处理对象与数据源还有各项配置，生成带有所需方法及参数的上下文
         *
         * @param processContext 上下文
         * @return org.springframework.expression.spel.support.StandardEvaluationContext
         * @author huangchengxing
         * @date 2022/6/4 21:34
         */
        StandardEvaluationContext get(ProcessContext processContext);

    }

    public static class DefaultContextFactory implements ContextFactory {

        @Override
        public StandardEvaluationContext get(ProcessContext processContext) {
            Object sourceData = processContext.getSourceData();
            Object target = processContext.getTarget();
            AssembleProperty property = processContext.getProperty();
            AssembleOperation operation = processContext.getOperation();

            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setVariable("target", target);
            context.setVariable("source", sourceData);
            context.setVariable("key", operation.getAssembler().getKey(target, operation));
            context.setVariable("src", property.getSource());
            context.setVariable("ref", property.getReference());

            return context;
        }
    }

}
