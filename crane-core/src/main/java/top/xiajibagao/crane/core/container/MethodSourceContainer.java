package top.xiajibagao.crane.core.container;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.CharSequenceUtil;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import top.xiajibagao.crane.core.annotation.MappingType;
import top.xiajibagao.crane.core.annotation.MethodSourceBean;
import top.xiajibagao.crane.core.component.BeanProperty;
import top.xiajibagao.crane.core.component.BeanPropertyFactory;
import top.xiajibagao.crane.core.helper.reflex.AsmReflexUtils;
import top.xiajibagao.crane.core.helper.reflex.IndexedMethod;
import top.xiajibagao.crane.core.helper.reflex.ReflexUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author huangchengxing
 * @date 2022/03/31 21:40
 */
@Slf4j
@RequiredArgsConstructor
public class MethodSourceContainer extends BaseNamespaceContainer<Object, Object> implements Container {

    public final Map<String, MethodSource> methodCache = new HashMap<>();
    private final BeanPropertyFactory beanPropertyFactory;

    /**
     * 注销已注册方法数据源
     *
     * @param namespace 命名空间
     * @author huangchengxing
     * @date 2022/6/1 8:01
     * @since 0.5.4
     */
    public void unregister(String namespace) {
        methodCache.remove(namespace);
    }
    
    /**
     * 注册方法数据源
     *
     * @param methodSourceBean 方法数据源
     * @author huangchengxing
     * @date 2022/6/1 9:23
     */
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
        MethodSourceBean bean = AnnotatedElementUtils.findMergedAnnotation(targetClass, MethodSourceBean.class);
        if (Objects.isNull(bean)) {
            return true;
        }
        MethodSourceBean.Method[] annotations = bean.methods();
        for (MethodSourceBean.Method annotation : annotations) {
            if (CharSequenceUtil.isBlank(annotation.name())) {
                continue;
            }
            Method method = ReflexUtils.findMethod(targetClass, annotation.name(),
                true, annotation.returnType(), annotation.paramTypes()
            );
            if (Objects.nonNull(method)) {
                registerMethod(methodSourceBean, targetClass, annotation, method);
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
            registerMethod(methodSourceBean, targetClass, annotation, proxyMethod);
        });
    }

    /**
     * 注册方法
     */
    private void registerMethod(Object methodSourceBean, Class<?> targetClass, MethodSourceBean.Method annotation, Method method) {
        checkMethod(method, annotation.namespace());
        beanPropertyFactory.getProperty(annotation.sourceType(), annotation.sourceKey())
            .ifPresent(property -> {
                MethodSource cache = new MethodSource(
                    annotation.mappingType(), methodSourceBean, targetClass, annotation.namespace(),
                    method, AsmReflexUtils.findMethod(targetClass, method), property
                );
                methodCache.put(annotation.namespace(), cache);
                log.info("注册方法数据源：[{}], 映射类型：[{}]", annotation.namespace(), annotation.mappingType().name());
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
    protected Map<String, Map<Object, Object>> getSources(@Nonnull Multimap<String, Object> namespaceAndKeys) {
        Map<String, Map<Object, Object>> results = new HashMap<>(namespaceAndKeys.size());
        namespaceAndKeys.asMap().forEach((namespace, keys) -> {
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
        private final IndexedMethod indexedMethod;
        private final BeanProperty sourceKeyProperty;

        @SuppressWarnings("unchecked")
        public Collection<Object> getSources(Collection<Object> keys) {
            return (Collection<Object>)indexedMethod.invoke(target, Convert.convert(sourceGetter.getParameterTypes()[0], keys));
        }

        public Object getSourceKeyPropertyValue(Object source) {
            return sourceKeyProperty.getValue(source);
        }

    }
}
