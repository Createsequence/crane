package top.xiajibagao.crane.extension.container;

import cn.hutool.core.collection.CollStreamUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;
import top.xiajibagao.annotation.MethodSource;
import top.xiajibagao.annotation.MethodSourceBean;
import top.xiajibagao.crane.core.container.BaseNamespaceContainer;
import top.xiajibagao.crane.core.helper.BeanProperty;
import top.xiajibagao.crane.core.helper.ReflexUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author huangchengxing
 * @date 2022/03/31 21:40
 */
@RequiredArgsConstructor
public class MethodSourceContainer extends BaseNamespaceContainer<Object, Object> {

    public final Map<String, MethodCache> methodCache = new HashMap<>();

    public void register(Object methodSourceBean) {
        if (Objects.isNull(methodSourceBean)) {
            return;
        }

        Class<?> targetClass = methodSourceBean.getClass();
        // 解析类注解
        MethodSourceBean classAnnotation = AnnotatedElementUtils.findMergedAnnotation(targetClass, MethodSourceBean.class);
        if (Objects.isNull(classAnnotation)) {
            return;
        }
        MethodSourceBean.Method[] classMethods = classAnnotation.methods();
        for (MethodSourceBean.Method classMethod : classMethods) {
            Method method = ReflexUtils.findMethod(
                targetClass, classMethod.name(),
                true, classMethod.returnType(), classMethod.paramTypes()
            );
            if (Objects.nonNull(method)) {
                checkMethod(method, classMethod.namespace());
                ReflexUtils.findProperty(classMethod.sourceType(), classMethod.sourceKey())
                    .ifPresent(pc -> {
                        MethodCache cache = new MethodCache(methodSourceBean, targetClass, classMethod.namespace(), method, pc);
                        methodCache.put(classMethod.namespace(), cache);
                    });
            }
        }

        // 获取被代理类方法
        List<Method> annotatedMethods = Stream.of(targetClass.getDeclaredMethods())
            .filter(m -> AnnotatedElementUtils.hasAnnotation(m, MethodSource.class))
            .collect(Collectors.toList());
        annotatedMethods.forEach(proxyMethod -> {
            Method actualMethod = AopUtils.getMostSpecificMethod(proxyMethod, targetClass);
            MethodSource annotation = AnnotatedElementUtils.findMergedAnnotation(actualMethod, MethodSource.class);
            if (Objects.isNull(annotation)) {
                return;
            }
            checkMethod(proxyMethod, annotation.namespace());
            ReflexUtils.findProperty(annotation.sourceType(), annotation.sourceKey())
                .ifPresent(pc -> {
                    MethodCache method = new MethodCache(methodSourceBean, targetClass, annotation.namespace(), proxyMethod, pc);
                    methodCache.put(annotation.namespace(), method);
                });
        });

    }

    private void checkMethod(Method declaredMethod, String containerName) {
        Assert.isTrue(!methodCache.containsKey(containerName), "容器方法已经被注册: " + containerName);
        Assert.isTrue(
            declaredMethod.getParameterTypes().length == 1
                && ClassUtils.isAssignable(Collection.class, declaredMethod.getParameterTypes()[0]),
            "容器方法有且仅能有一个Collection类型的参数: " + Arrays.asList(declaredMethod.getParameterTypes())
        );
        Assert.isTrue(
            ClassUtils.isAssignable(Collection.class, declaredMethod.getReturnType()),
            "容器方法的返回值必须为Collection类型: " + declaredMethod.getReturnType()
        );
    }

    @Override
    protected Map<String, Map<Object, Object>> getSources(MultiValueMap<String, Object> namespaceAndKeys) {
        Map<String, Map<Object, Object>> results = new HashMap<>(namespaceAndKeys.size());
        namespaceAndKeys.forEach((namespace, keys) -> {
            MethodCache method = methodCache.get(namespace);
            if (Objects.isNull(method)) {
                return;
            }
            Collection<Object> sources = method.getSources(keys);
            Map<Object, Object> sourceMap = CollStreamUtil.toMap(sources, method::getSourceKeyProperty, Function.identity());
            results.put(namespace, sourceMap);
        });
        return results;
    }

    /**
     * @author huangchengxing
     * @date 2022/03/31 21:26
     */
    @RequiredArgsConstructor
    public static class MethodCache {

        private final Object target;
        private final Class<?> targetClass;
        private final String containerName;
        private final Method sourceGetter;
        private final BeanProperty sourceKeyProperty;

        @SuppressWarnings("unchecked")
        public Collection<Object> getSources(List<Object> keys) {
            Collection<Object> params = keys;
            if (Objects.equals(sourceGetter.getParameterTypes()[0], Set.class)) {
                params = new HashSet<>(keys);
            }
            return (Collection<Object>) ReflectionUtils.invokeMethod(sourceGetter, target, params);
        }

        public Object getSourceKeyProperty(Object source) {
            return ReflectionUtils.invokeMethod(sourceKeyProperty.getter(), source);
        }

    }
}
