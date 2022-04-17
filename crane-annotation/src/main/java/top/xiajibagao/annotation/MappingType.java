package top.xiajibagao.annotation;

import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * 字段映射类型，表明数据源对象与装配操作对应的key字段值的映射关系
 *
 * @author huangchengxing
 * @date 2022/04/11 0:08
 */
@RequiredArgsConstructor
public enum MappingType {

    /**
     * 一个key对应多个数据源对象
     */
    ONE_TO_ONE((sources, keyMapper) -> CollectionUtils.isEmpty(sources) ?
        Collections.emptyMap() : sources.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(keyMapper, Function.identity()))),

    /**
     * 一个key对应一个数据源对象
     */
    ONE_TO_MORE((sources, keyMapper) -> {
        if (CollectionUtils.isEmpty(sources)) {
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
