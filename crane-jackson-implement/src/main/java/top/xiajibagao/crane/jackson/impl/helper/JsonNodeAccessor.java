package top.xiajibagao.crane.jackson.impl.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;

/**
 * JsonNode对象属性访问器，注册到SpEL上下文中，用于通过类似“xxx.xxx”的表达式读写对象属性
 *
 * @author huangchengxing
 * @date 2022/06/04 22:11
 */
@RequiredArgsConstructor
public class JsonNodeAccessor implements PropertyAccessor {

    private final ObjectMapper objectMapper;

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class[]{ JsonNode.class };
    }

    @Override
    public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
        return JacksonUtils.isNodeAndNotNull(target) && ((JsonNode) target).has(name);
    }

    @Override
    public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
        return new TypedValue(((JsonNode) target).get(name));
    }

    @Override
    public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
        return target instanceof ObjectNode;
    }

    @Override
    public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
        JsonNode jsonNode = objectMapper.valueToTree(newValue);
        ((ObjectNode)target).set(name, jsonNode);
    }

}
