package top.xiajibagao.crane.core.helper.reflex;

import com.esotericsoftware.reflectasm.MethodAccess;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangchengxing
 * @date 2022/05/09 16:37
 */
public class AsmReflexUtils {

    private static final Map<Class<?>, MethodAccess> METHOD_ACCESS_CACHE = new ConcurrentHashMap<>();
    private static final BeanPropertyFactory ASM_REFLEX_PROPERTY_FACTORY = new BeanPropertyFactory(
        (targetClass, field) -> {
            int getterIndex = findGetterMethodIndex(targetClass, field.getName());
            Assert.isTrue(getterIndex > -1, String.format("属性[%s]找不到对应的Getter方法", field));
            int setterIndex = findSetterMethodIndex(targetClass, field.getName(), field.getType());
            Assert.isTrue(setterIndex > -1, String.format("属性[%s]找不到对应的Setter方法", field));
            MethodAccess methodAccess = getMethodAccess(targetClass);
            return new AsmReflexBeanProperty(
                targetClass, field, new IndexedMethod(methodAccess, getterIndex), new IndexedMethod(methodAccess, setterIndex)
            );
        }
    );

    private AsmReflexUtils() {
    }

    /**
     * 获取字段缓存
     *
     * @param targetClass 类
     * @param fieldName 属性名
     * @throws IllegalArgumentException 当属性存在，而却找不到对应setter与getter方法时抛出
     * @return cn.net.nova.crane.helper.PropertyUtils.PropertyCache
     * @author huangchengxing
     * @date 2022/4/1 13:50
     */
    @Nonnull
    public static Optional<BeanProperty> findProperty(Class<?> targetClass, String fieldName) {
        return ASM_REFLEX_PROPERTY_FACTORY.getProperty(targetClass, fieldName);
    }

    /**
     * 从指定类及其父类中寻找指定属性的setter方法的访问下标
     * <ul>
     *     <li>优先寻找格式为“setFieldName”，并且有且仅有一个类型为fieldType的方法；</li>
     *     <li>若找不到，再寻找格式为“fieldName”，并且有且仅有一个类型为fieldType的方法；</li>
     * </ul>
     *
     * @param targetClass 属性
     * @param fieldName 属性名
     * @return int setter方法的访问下标，若不存在则返回-1
     * @author huangchengxing
     * @date 2022/4/1 12:58
     */
    public static int findSetterMethodIndex(Class<?> targetClass, String fieldName, Class<?> fieldType) {
        MethodAccess methodAccess = getMethodAccess(targetClass);
        Method setter = ReflexUtils.findSetterMethod(targetClass, fieldName, fieldType);
        return Objects.isNull(setter) ? -1 : methodAccess.getIndex(setter.getName(), setter.getParameterTypes());
    }

    /**
     * 从指定类及其父类中寻找指定属性的getter方法的访问下标：
     * <ul>
     *     <li>优先寻找格式为“getFieldName”，并且没有参数的方法；</li>
     *     <li>若找不到，再寻找格式为“isFieldName”，并且没有参数的方法；</li>
     *     <li>仍然找不到，再寻找格式为“fieldName”，并且没有参数的方法；</li>
     * </ul>
     *
     * @param targetClass 类
     * @param fieldName 属性名
     * @return int getter方法的访问下标，若不存在则返回-1
     * @author huangchengxing
     * @date 2022/5/9 17:12
     */
    public static int findGetterMethodIndex(Class<?> targetClass, String fieldName) {
        MethodAccess methodAccess = getMethodAccess(targetClass);
        Method getter = ReflexUtils.findGetterMethod(targetClass, fieldName);
        return Objects.isNull(getter) ? -1 : methodAccess.getIndex(getter.getName(), getter.getParameterTypes());
    }

    /**
     * 获取{@link MethodAccess}
     *
     * @param targetClass 目标类型
     * @return com.esotericsoftware.reflectasm.MethodAccess
     * @author huangchengxing
     * @date 2022/5/9 17:06
     */
    public static MethodAccess getMethodAccess(Class<?> targetClass) {
        return METHOD_ACCESS_CACHE.computeIfAbsent(targetClass, MethodAccess::get);
    }
    
    /**
     * 获取指定方法
     *
     * @param targetClass 目标类型
     * @param methodName 方法名称
     * @param paramTypes 方法参数类型
     * @return top.xiajibagao.crane.core.helper.reflex.AsmReflexUtils.IndexedMethod
     * @author huangchengxing
     * @date 2022/5/9 17:44
     */
    public static IndexedMethod findMethod(Class<?> targetClass, String methodName, Class<?>... paramTypes) {
        MethodAccess methodAccess = MethodAccess.get(targetClass);
        return new IndexedMethod(methodAccess, methodAccess.getIndex(methodName, paramTypes));
    }

}
