package top.xiajibagao.crane.core.handler;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import top.xiajibagao.crane.core.helper.ExpressionUtils;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.AssembleProperty;

import java.util.List;
import java.util.function.Supplier;

/**
 * 带SpEL表达式的字段处理器 <br />
 * 该处理器允许被被视为执行器链的代理类，亦或者视为一个处理器节点，此时该节点总是应当在处理器链的最前端。
 * 当向待处理对象写入数据时，该处理器将拦截数据并执行表达式获取返回值作为新的数据源，
 * 并再次调用处理器链尝试写入该值
 *
 * @author huangchengxing
 * @date 2022/04/13 0:06
 */
@Getter
@RequiredArgsConstructor
public class ExpressionAssembleHandler implements AssembleHandler, AssembleHandlerChain {

    private final AssembleHandlerChain handlerChain;
    private final Supplier<StandardEvaluationContext> contextFactory;

    @Override
    public int order() {
        return -1;
    }

    @Override
    public List<AssembleHandler> handlers() {
        return handlerChain.handlers();
    }

    @Override
    public AssembleHandlerChain addHandler(AssembleHandler handler) {
        return handlerChain.addHandler(handler);
    }

    @Override
    public boolean sourceCanRead(Object source, AssembleProperty property, AssembleOperation operation) {
        return handlerChain.sourceCanRead(source, property, operation);
    }

    @Override
    public boolean targetCanWrite(Object sourceData, Object target, AssembleProperty property, AssembleOperation operation) {
        return handlerChain.targetCanWrite(sourceData, target, property, operation);
    }

    @Override
    public Object readFromSource(Object source, AssembleProperty property, AssembleOperation operation) {
        return handlerChain.readFromSource(source, property, operation);
    }

    @Override
    public void writeToTarget(Object sourceData, Object target, AssembleProperty property, AssembleOperation operation) {
        if (CharSequenceUtil.isBlank(property.getExp())) {
            handlerChain.writeToTarget(sourceData, target, property, operation);
            return;
        }
        StandardEvaluationContext context = contextFactory.get();
        context.setVariable("target", target);
        context.setVariable("source", sourceData);
        context.setVariable("key", operation.getAssembler().getKey(target, operation));
        context.setVariable("src", property.getResource());
        context.setVariable("ref", property.getReference());
        sourceData = ExpressionUtils.execute(property.getExp(), property.getExpType(), true);
        handlerChain.writeToTarget(sourceData, target, property, operation);
    }
}
