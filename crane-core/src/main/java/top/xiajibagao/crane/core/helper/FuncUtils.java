package top.xiajibagao.crane.core.helper;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;

import java.lang.invoke.SerializedLambda;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 函数式接口工具类
 *
 * @author huangchengxing
 * @date 2022/04/09 21:38
 */
public abstract class FuncUtils {

    private static final Map<String , SerializedLambda> SERIALIZED_LAMBDA_CACHE = new ConcurrentHashMap<>();
    private static final String WRITE_REPLACE = "writeReplace";

    private FuncUtils() {}

    /**
     * 获取Lambda表达式对应的字段名称
     * <ul>
     *     <li>若以格式为“getXxx/setXxx”，则返回“xxx”；</li>
     *     <li>若以格式为“isXxx”，则返回“xxx”；</li>
     *     <li>其余格式直接返回方法名；</li>
     * </ul>
     *
     * @param func 可序列化的函数式接口
     * @return java.lang.String
     * @author huangchengxing
     * @date 2022/4/9 21:47
     */
    public static <P> String getPropertyName(SFunc<P, ?> func) {
        String methodName = getMethodName(func);
        if (methodName.startsWith(ReflexUtils.GET_PREFIX) || methodName.startsWith(ReflexUtils.SET_PREFIX)) {
            return CharSequenceUtil.removePreAndLowerFirst(methodName, 3);
        } else if (methodName.startsWith(ReflexUtils.IS_PREFIX)) {
            return CharSequenceUtil.removePreAndLowerFirst(methodName, 2);
        }
        return methodName;
    }
    
    /**
     * 获取Lambda表达式方法名
     *
     * @param func 可序列化的函数式接口
     * @return java.lang.String
     * @author huangchengxing
     * @date 2022/4/9 21:47
     */
    public static <P> String getMethodName(SFunc<P, ?> func) {
        return resolve(func).getImplMethodName();
    }

    /**
     * 获取{@link SerializedLambda}
     *
     * @param func 可序列化的函数式接口
     * @return java.lang.invoke.SerializedLambda
     * @author huangchengxing
     * @date 2022/4/9 21:43
     */
    public static <P> SerializedLambda resolve(SFunc<P, ?> func) {
        return SERIALIZED_LAMBDA_CACHE.computeIfAbsent(
            func.getClass().getName(), f -> ReflectUtil.invoke(func, WRITE_REPLACE)
        );
    }

}
