package top.xiajibagao.annotation;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * @author huangchengxing
 * @date 2022/04/11 0:08
 */
@RequiredArgsConstructor
public enum MappingType {

    /**
     * 一个key对应多个数据源对象
     */
    ONE_TO_ONE((sources, keyMapper) -> CollUtil.isNotEmpty(sources) ? sources.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(keyMapper, Function.identity())) : Collections.emptyMap()),

    /**
     * 一个key对应一个数据源对象
     */
    ONE_TO_MORE((sources, keyMapper) -> {
        if (CollUtil.isEmpty(sources)) {
            return Collections.emptyMap();
        }
        Map<Object, Object> results = new HashMap<>();
        sources.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(keyMapper))
            .forEach(results::put);
        return results;
    });

    private final BiFunction<Collection<Object>, UnaryOperator<Object>, Map<Object, Object>> mapper;

    public Map<Object, Object> mapping(Collection<Object> sources, UnaryOperator<Object> keyMapper) {
        return mapper.apply(sources, keyMapper);
    }

}
