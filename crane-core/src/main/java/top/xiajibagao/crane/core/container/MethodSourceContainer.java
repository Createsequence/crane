package top.xiajibagao.crane.core.container;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;
import top.xiajibagao.crane.core.annotation.MappingType;
import top.xiajibagao.crane.core.annotation.MethodSourceBean;
import top.xiajibagao.crane.core.helper.AsmReflexUtils;
import top.xiajibagao.crane.core.helper.BeanProperty;
import top.xiajibagao.crane.core.helper.ReflexUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author huangchengxing
 * @date 2022/03/31 21:40
 */
@RequiredArgsConstructor
public class MethodSourceContainer extends BaseNamespaceContainer<Object, Object> implements Container {

    public final Map<String, MethodSource> methodCache = new HashMap<>();

    public void register(Object methodSourceBean) {
        if (Objects.isNull(methodSourceBean)) {
            return;
        }

        Class<?> targetClass = methodSourceBean.getClass();
        if (parseClassAnnotation(methodSourceBean, targetClass)) {
            return;
        }
        parseMethodAnnotations(methodSourceBean, targetClass);
    }

    /**
     * 解析{@link MethodSourceBean}注解中声明的方法
     */
    private boolean parseClassAnnotation(Object methodSourceBean, Class<?> targetClass) {
        MethodSourceBean annotation = AnnotatedElementUtils.findMergedAnnotation(targetClass, MethodSourceBean.class);
        if (Objects.isNull(annotation)) {
            return true;
        }
        MethodSourceBean.Method[] classMethods = annotation.methods();
        for (MethodSourceBean.Method classMethod : classMethods) {
            if (CharSequenceUtil.isBlank(classMethod.name())) {
                continue;
            }
            Method method = ReflexUtils.findMethod(targetClass, classMethod.name(),
                true, classMethod.returnType(), classMethod.paramTypes()
            );
            if (Objects.nonNull(method)) {
                checkMethod(method, classMethod.namespace());
                AsmReflexUtils.findProperty(classMethod.sourceType(), classMethod.sourceKey())
                    .ifPresent(property -> {
                        MethodSource cache = new MethodSource(classMethod.mappingType(), methodSourceBean, targetClass, classMethod.namespace(), method, property);
                        methodCache.put(classMethod.namespace(), cache);
                    });
            }
        }
        return false;
    }

    /**
     * 解析被{@link MethodSourceBean.Method}注解的方法
     */
    private void parseMethodAnnotations(Object methodSourceBean, Class<?> targetClass) {
        List<Method> annotatedMethods = Stream.of(targetClass.getDeclaredMethods())
            .filter(m -> AnnotatedElementUtils.hasAnnotation(m, MethodSourceBean.Method.class))
            .collect(Collectors.toList());
        annotatedMethods.forEach(proxyMethod -> {
            Method actualMethod = AopUtils.getMostSpecificMethod(proxyMethod, targetClass);
            MethodSourceBean.Method annotation = AnnotatedElementUtils.findMergedAnnotation(actualMethod, MethodSourceBean.Method.class);
            if (Objects.isNull(annotation)) {
                return;
            }
            checkMethod(proxyMethod, annotation.namespace());
            AsmReflexUtils.findProperty(annotation.sourceType(), annotation.sourceKey())
                .ifPresent(property -> {
                    MethodSource method = new MethodSource(annotation.mappingType(), methodSourceBean, targetClass, annotation.namespace(), proxyMethod, property);
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

    @Nonnull
    @Override
    protected Map<String, Map<Object, Object>> getSources(@Nonnull MultiValueMap<String, Object> namespaceAndKeys) {
        Map<String, Map<Object, Object>> results = new HashMap<>(namespaceAndKeys.size());
        namespaceAndKeys.forEach((namespace, keys) -> {
            MethodSource method = methodCache.get(namespace);
            if (Objects.isNull(method)) {
                return;
            }
            Collection<Object> sources = method.getSources(keys);
            if (CollUtil.isEmpty(sources)) {
                return;
            }
            results.put(namespace, method.getMappingType().mapping(sources, method::getSourceKeyPropertyValue));
        });
        return results;
    }

    /**
     * @author huangchengxing
     * @date 2022/03/31 21:26
     */
    @RequiredArgsConstructor
    public static class MethodSource {

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
            return sourceKeyProperty.getValue(source);
        }

    }
}
