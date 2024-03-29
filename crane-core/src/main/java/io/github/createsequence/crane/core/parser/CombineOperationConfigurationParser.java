package io.github.createsequence.crane.core.parser;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import io.github.createsequence.crane.core.parser.interfaces.OperateConfigurationParser;
import io.github.createsequence.crane.core.parser.interfaces.OperationConfiguration;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 组合注解配置解析器解析器，解析时，将调用全部已注册的解析器，
 * 然后合并得到的配置类中{@link OperationConfiguration#getAssembleOperations()}
 * 与{@link OperationConfiguration#getDisassembleOperations()}
 *
 * @author huangchengxing
 * @date 2022/05/22 17:11
 * @since 0.5.0
 */
public class CombineOperationConfigurationParser implements OperateConfigurationParser {

    private final List<OperateConfigurationParser> parserChain = new ArrayList<>();

    /**
     * 添加解析器
     *
     * @param parser 解析器
     * @return parser.io.github.createsequence.crane.core.CombineOperationConfigurationParser
     * @author huangchengxing
     * @date 2022/5/22 17:14
     */
    public CombineOperationConfigurationParser addParser(OperateConfigurationParser parser) {
        parserChain.add(parser);
        return this;
    }

    @Nonnull
    @Override
    public OperationConfiguration parse(Class<?> targetClass) {
        List<OperationConfiguration> configurations = CollStreamUtil.toList(parserChain, p -> p.parse(targetClass));
        OperationConfiguration result = CollUtil.getFirst(configurations);
        Objects.requireNonNull(result);
        configurations.stream()
            .skip(1L)
            .forEach(conf -> {
                result.getAssembleOperations().addAll(conf.getAssembleOperations());
                result.getDisassembleOperations().addAll(conf.getDisassembleOperations());
            });
        return result;
    }

}
