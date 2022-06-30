package top.xiajibagao.crane.core.container;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import top.xiajibagao.crane.core.annotation.Assemble;
import top.xiajibagao.crane.core.annotation.MappingType;
import top.xiajibagao.crane.core.annotation.MethodSourceBean;
import top.xiajibagao.crane.core.helper.invoker.MethodInvoker;
import top.xiajibagao.crane.core.helper.property.BeanProperty;
import top.xiajibagao.crane.core.helper.property.BeanPropertyFactory;
import top.xiajibagao.crane.core.helper.reflex.AsmReflexUtils;
import top.xiajibagao.crane.core.helper.reflex.ReflexUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 方法数据源容器
 *
 * <p>表示以实例方法作为数据源的数据源容器。内部维护有方法实例与命名空间的映射表。
 * 当spring启动时，将扫描容器中类上带有{@link MethodSourceBean}注解的实例，
 * 并通过{@link #register(Object)}将其注册到方法数据源容器中。<br />
 * 方法数据源会进一步解析该示例中被{@link MethodSourceBean.Method}注解的方法，
 * 按照各自{@link MethodSourceBean.Method#namespace()}
 * 缓存到不同的命名空间中。
 *
 * <p>容器中作为数据源的方法应当有且仅有一个{@link Collection}及其子类型的入参，
 * 当调用时，会将一批对象中{@link Assemble}指定的key集合作为参数，传入命名空间对的方法。
 * 方法的返回也应当为{@link Collection}集合或其子类。
 *
 * <p>容器根据{@link MethodSourceBean.Method#mappingType()}属性指定的{@link MappingType}枚举，
 * 支持按照{@link MethodSourceBean.Method#sourceKey()}指定的key对数据源一对多或多对一。<br />
 * 当指定为{@link MappingType#ONE_TO_ONE}时，容器根据key批量获取的数据源默认将会按一对一分组，
 * 当出现复数数据源对一个key值时将会抛出异常。此时装配器根据key获得到的值为数据源对象本身。<br />
 * 当指定为{@link MappingType#ONE_TO_MORE}时，容器将按key分组，此时装配器根据key获得到的值为数据源对象集合。
 *
 * @author huangchengxing
 * @date 2022/03/31 21:40
 * @see MethodSourceBean
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
        Set<MethodSourceBean> annotations = AnnotatedElementUtils.findAllMergedAnnotations(targetClass, MethodSourceBean.class);
        if (CollUtil.isEmpty(annotations)) {
            return;
        }
        parseClassAnnotation(methodSourceBean, targetClass, annotations);
        parseMethodAnnotations(methodSourceBean, targetClass);
    }

    /**
     * 解析{@link MethodSourceBean}注解中声明的方法
     */
    private void parseClassAnnotation(Object methodSourceBean, Class<?> targetClass, Set<MethodSourceBean> annotations) {
        annotations.stream()
            .map(MethodSourceBean::value)
            .flatMap(Stream::of)
            .filter(annotation -> CharSequenceUtil.isNotBlank(annotation.name()))
            .forEach(annotation -> {
                Method method = ReflexUtils.findMethod(targetClass, annotation.name(),
                    true, annotation.returnType(), annotation.paramTypes()
                );
                if (Objects.nonNull(method)) {
                    registerMethod(methodSourceBean, targetClass, annotation, method);
                }
            });
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
                    AsmReflexUtils.findMethod(targetClass, method, true), property,
                    method.getParameterTypes().length == 0
                );
                methodCache.put(annotation.namespace(), cache);
                log.info("注册方法数据源：[{}], 映射类型：[{}]", annotation.namespace(), annotation.mappingType().name());
            });
    }

    private void checkMethod(Method declaredMethod, String containerName) {
        Assert.isTrue(!methodCache.containsKey(containerName), "容器方法已经被注册: " + containerName);
        Assert.isTrue(
            declaredMethod.getParameterTypes().length == 0
                || (declaredMethod.getParameterTypes().length == 1 && ClassUtils.isAssignable(Collection.class, declaredMethod.getParameterTypes()[0])),
            "容器方法最多仅能有一个Collection类型的参数: " + Arrays.asList(declaredMethod.getParameterTypes())
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
        private final MethodInvoker methodInvoker;
        private final BeanProperty sourceKeyProperty;
        private final boolean isNotArgMethod;

        @SuppressWarnings("unchecked")
        public Collection<Object> getSources(Collection<Object> keys) {
            Object result = isNotArgMethod ?
                methodInvoker.invoke(target) : methodInvoker.invoke(target, keys);
            return (Collection<Object>)result;
        }

        public Object getSourceKeyPropertyValue(Object source) {
            return sourceKeyProperty.getValue(source);
        }

    }
}
