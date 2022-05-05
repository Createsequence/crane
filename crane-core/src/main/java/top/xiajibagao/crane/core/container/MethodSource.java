package top.xiajibagao.crane.core.container;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ReflectionUtils;
import top.xiajibagao.crane.core.annotation.MappingType;
import top.xiajibagao.crane.core.helper.BeanProperty;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author huangchengxing
 * @date 2022/03/31 21:26
 */
@RequiredArgsConstructor
public class MethodSource {

    @Getter
    private final MappingType mappingType;
    private final Object target;
    @Getter
    private final Class<?> targetClass;
    @Getter
    private final String containerName;
    private final Method sourceGetter;
    private final BeanProperty sourceKeyProperty;

    @SuppressWarnings("unchecked")
    public Collection<Object> getSources(List<Object> keys) {
        Collection<Object> params = keys;
        if (Objects.equals(sourceGetter.getParameterTypes()[0], Set.class)) {
            params = new HashSet<>(keys);
        }
        return (Collection<Object>)ReflectionUtils.invokeMethod(sourceGetter, target, params);
    }

    public Object getSourceKeyPropertyValue(Object source) {
        return ReflectionUtils.invokeMethod(sourceKeyProperty.getter(), source);
    }

}
