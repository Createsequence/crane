package top.xiajibagao.crane.core.helper.reflex;

import com.esotericsoftware.reflectasm.MethodAccess;
import top.xiajibagao.crane.core.helper.invoker.AsmReflexMethodInvoker;
import top.xiajibagao.crane.core.helper.invoker.MethodInvoker;
import top.xiajibagao.crane.core.helper.invoker.ParamTypeAutoConvertInvoker;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字节码工具类
 *
 * @author huangchengxing
 * @date 2022/05/09 16:37
 */
public class AsmReflexUtils {

    private static final Map<Class<?>, MethodAccess> METHOD_ACCESS_CACHE = new ConcurrentHashMap<>();

    private AsmReflexUtils() {
    }

    /**
     * 从指定类及其父类中寻找指定属性的setter方法的访问下标
     * <ul>
     *     <li>优先寻找格式为“setFieldName”，并且有且仅有一个类型为fieldType的方法；</li>
     *     <li>若找不到，再寻找格式为“fieldName”，并且有且仅有一个类型为fieldType的方法；</li>
     * </ul>
     *
     * @param targetClass 属性
     * @param fieldName   属性名
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
     * @param fieldName   属性名
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
     * @param method 目标方法
     * @param enableParamTypeConvert 是否允许自动转换入参类型
     * @return top.xiajibagao.crane.core.helper.invoker.MethodInvoker
     * @author huangchengxing
     * @date 2022/5/9 17:44
     * @since 0.5.5
     */
    public static MethodInvoker findMethod(Class<?> targetClass, Method method, boolean enableParamTypeConvert) {
        MethodAccess methodAccess = MethodAccess.get(targetClass);
        MethodInvoker methodInvoker = new AsmReflexMethodInvoker(methodAccess, methodAccess.getIndex(method.getName(), method.getParameterTypes()));
        return enableParamTypeConvert ? new ParamTypeAutoConvertInvoker(method.getParameterTypes(), methodInvoker) : methodInvoker;
    }

    /**
     * 获取指定方法
     *
     * @param targetClass 目标类型
     * @param method 目标方法
     * @return top.xiajibagao.crane.core.helper.invoker.MethodInvoker
     * @author huangchengxing
     * @date 2022/5/9 17:44
     */
    public static MethodInvoker findMethod(Class<?> targetClass, Method method) {
        return findMethod(targetClass, method, false);
    }
}