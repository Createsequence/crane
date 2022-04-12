package top.xiajibagao.crane.extension.container;

import cn.hutool.core.collection.CollStreamUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import top.xiajibagao.annotation.MethodSourceBean;
import top.xiajibagao.crane.core.container.BaseNamespaceContainer;
import top.xiajibagao.crane.core.helper.ReflexUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 方法数据源容器
 *
 * @author huangchengxing
 * @date 2022/03/31 21:40
 */
@RequiredArgsConstructor
public class MethodSourceContainer extends BaseNamespaceContainer<Object, Object> {

    public final Map<String, MethodSource> methodSourceCache = new HashMap<>();

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
        registerClassAnnotatedDeclarativeMethods(methodSourceBean, targetClass, classAnnotation);
        registerAnnotatedMethods(methodSourceBean, targetClass);
    }

    /**
     * 注册在方法注解中声明的方法
     */
    private void registerClassAnnotatedDeclarativeMethods(Object methodSourceBean, Class<?> targetClass, MethodSourceBean classAnnotation) {
        MethodSourceBean.Method[] classMethods = classAnnotation.methods();
        for (MethodSourceBean.Method classMethod : classMethods) {
            Method method = ReflexUtils.findMethod(targetClass, classMethod.name(),
                true, classMethod.returnType(), classMethod.paramTypes()
            );
            if (Objects.nonNull(method)) {
                checkMethod(method, classMethod.namespace());
                ReflexUtils.findProperty(classMethod.sourceType(), classMethod.sourceKey())
                    .ifPresent(pc -> {
                        MethodSource cache = new MethodSource(methodSourceBean, targetClass, classMethod.namespace(), method, pc);
                        methodSourceCache.put(classMethod.namespace(), cache);
                    });
            }
        }
    }

    /**
     * 注册被注解的方法
     */
    private void registerAnnotatedMethods(Object methodSourceBean, Class<?> targetClass) {
        // 获取被代理类方法
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
            ReflexUtils.findProperty(annotation.sourceType(), annotation.sourceKey())
                .ifPresent(pc -> {
                    MethodSource method = new MethodSource(methodSourceBean, targetClass, annotation.namespace(), proxyMethod, pc);
                    methodSourceCache.put(annotation.namespace(), method);
                });
        });
    }

    private void checkMethod(Method declaredMethod, String containerName) {
        Assert.isTrue(!methodSourceCache.containsKey(containerName), "容器方法已经被注册: " + containerName);
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
            MethodSource method = methodSourceCache.get(namespace);
            if (Objects.isNull(method)) {
                return;
            }
            Collection<Object> sources = method.getSources(keys);
            Map<Object, Object> sourceMap = CollStreamUtil.toMap(sources, method::getSourceKeyProperty, Function.identity());
            results.put(namespace, sourceMap);
        });
        return results;
    }

}
