package top.xiajibagao.crane.core.parser.interfaces;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.core.annotation.Order;
import top.xiajibagao.crane.core.helper.Orderly;
import top.xiajibagao.crane.core.operator.interfaces.Assembler;
import top.xiajibagao.crane.core.operator.interfaces.Disassembler;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * 操作配置解析器
 *
 * <p>将指定类型中的各项字段的装配与装卸操作配置解析并整合为操作配置。
 *
 * <p>解析器实现了{@link Orderly}接口，表明当存在多个解析器实例被一同调用时，
 * 若被 Spring 容器管理，则应当按照优先{@link Order}指定的顺序执行，否则应当按照{@link Orderly#compareTo(Orderly)}
 * 给定的自然顺序执行。
 *
 * @see OperationConfiguration
 * @see AssembleOperation
 * @see DisassembleOperation
 * @see Assembler
 * @see Disassembler
 * @author huangchengxing
 * @date 2022/03/01 15:52
 */
public interface OperateConfigurationParser extends Orderly {

    /**
     * 包下的类不需要解析
     */
    Collection<String> NOT_PARSE_PACKAGE = Collections.unmodifiableSet(CollUtil.newHashSet("java.", "javax.", "sum.", "org.springframework."));

    /**
     * 包括"java."、"javax."、"sum", "org.springframework."包下的类
     *
     * @param targetClass 目标类型
     * @return boolean
     * @author huangchengxing
     * @date 2022/6/24 15:26
     * @since 0.5.7
     */
    static boolean isNotParseClass(Class<?> targetClass) {
        return Objects.isNull(targetClass)
            || NOT_PARSE_PACKAGE.stream().anyMatch(path -> CharSequenceUtil.startWithAny(targetClass.getName(), path));
    }

    /**
     * 解析目标类型，获取该类型对应的类操作配置实例
     *
     * @param targetClass 目标类型
     * @return top.xiajibagao.crane.parse.interfaces.OperationConfiguration
     * @author huangchengxing
     * @date 2022/3/1 15:54
     */
    @Nonnull
    OperationConfiguration parse(Class<?> targetClass);

}
