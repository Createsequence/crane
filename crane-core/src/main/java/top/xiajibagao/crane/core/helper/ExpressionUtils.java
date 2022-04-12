package top.xiajibagao.crane.core.helper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SpEL表达式工具类
 *
 * @author huangchengxing
 * @date 2022/03/23 20:55
 */
public class ExpressionUtils {

    private ExpressionUtils() {
    }

    /**
     * 表达式解析器
     */
    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private static final Map<String, WeakReference<Expression>> EXPRESSION_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取通用表达式解析器
     *
     * @return org.springframework.expression.ExpressionParser
     * @author huangchengxing
     * @date 2022/3/23 21:37
     */
    public static ExpressionParser getParser() {
        return ExpressionUtils.PARSER;
    }

    /**
     * 解析表达式，并缓存表达式结果
     *
     * @param exp 表达式
     * @param cached 是否缓存表达式
     * @return org.springframework.expression.Expression
     * @author huangchengxing
     * @date 2021/10/11 14:24
     */
    public static Expression parseExp(@NonNull String exp, boolean cached) {
        return Optional.ofNullable(EXPRESSION_CACHE.get(exp))
            .map(WeakReference::get)
            .orElseGet(() -> {
                Expression expression = PARSER.parseExpression(exp);
                if (cached) {
                    EXPRESSION_CACHE.put(exp, new WeakReference<>(expression));
                }
                return expression;
            });
    }

    /**
     * 解析表达式并执行，然后返回指定类型结果
     *
     * @param exp 表达式
     * @param resultType 返回值类
     * @param cached 是否缓存表达式
     * @return T
     * @author huangchengxing
     * @date 2021/10/11 16:09
     */
    public static <T> T execute(String exp, Class<T> resultType, boolean cached) {
        return !StringUtils.hasText(exp) ? null : parseExp(exp, cached).getValue(resultType);
    }

    /**
     * 解析表达式并在指定上下文中执行，然后返回指定类型结果
     *
     * @param exp 表达式
     * @param context 上下文
     * @param resultType 返回值类
     * @param cached 是否缓存表达式
     * @return T
     * @author huangchengxing
     * @date 2021/10/11 16:09
     */
    public static <T> T execute(String exp, EvaluationContext context, Class<T> resultType, boolean cached) {
        return !StringUtils.hasText(exp) ? null : parseExp(exp, cached).getValue(context, resultType);
    }

    /**
     * 注册方法参数
     *
     * @param paramNames 参数名
     * @param args 参数
     * @param context 上下文
     * @return org.springframework.expression.EvaluationContext
     * @author huangchengxing
     * @date 2022/3/23 21:01
     */
    public static EvaluationContext registerMethodArgs(
        @NonNull Collection<String> paramNames, @NonNull Collection<Object> args, StandardEvaluationContext context) {
        Assert.isTrue(
            CollUtil.size(paramNames) == CollUtil.size(args),
            "参数名与参数个数不一致: [{}]/[{}]",
            CollUtil.size(paramNames), CollUtil.size(args)
        );
        CollUtil.zip(paramNames, args).forEach(context::setVariable);
        return context;
    }

}
