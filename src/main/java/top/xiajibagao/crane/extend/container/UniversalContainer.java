package top.xiajibagao.crane.extend.container;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import top.xiajibagao.crane.annotation.Assemble;
import top.xiajibagao.crane.helper.CollUtils;
import top.xiajibagao.crane.helper.TableMap;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 通用容器，使用方式类似MybatisPlus或JPA的通用底层，使用步骤如下：
 * <ul>
 *     <li>使需要作为数据源的类实现{@link UniversalBean}接口；</li>
 *     <li>使Mapper或Repository类实现{@link UniversalMapper}接口，并使其被Spring容器管理；</li>
 *     <li>
 *         在需要的字段上使用{@link Assemble}注解，并设置容器为{@link UniversalContainer},
 *         命名空间为{@link UniversalMapper}实现类在Spring容器中的名称；
 *     </li>
 * </ul>
 * 完成上述设置后，当执行处理时，会根据字段配置的命名空间获取对应的{@link UniversalMapper}，
 * 然后根据{@link UniversalMapper#getSourcesByIds(Collection)}方法查出数据，
 * 并按{@link UniversalBean#getId()}分组，然后再根据后续待处理字段的命名空间与key值获取数据源并处理。
 *
 * @author huangchengxing
 * @date 2022/03/24 20:40
 */
// TODO 提供基于注解的版本。使用注解标记标记指定getSourcesByIds方法和对象的key字段，然后在spring初始化式建立关联，表现类似SpringEvent或者Guava的EventBus
@Slf4j
@RequiredArgsConstructor
public class UniversalContainer<K> extends BaseContainer<K, UniversalBean<K>> {

    private final Map<String, UniversalMapper<K>> mappers;

    @Override
    protected Map<String, Map<K, UniversalBean<K>>> getSources(MultiValueMap<String, K> namespaceAndKeys) {
        // 获取数据源，并按namespace与id分组
        TableMap<String, K, UniversalBean<K>> sourceTable = new TableMap<>();
        namespaceAndKeys.forEach((namespace, keys) -> {
            if (CollUtils.isEmpty(keys)) {
                return;
            }
            UniversalMapper<K> mapper = mappers.get(namespace);
            if (Objects.isNull(mapper)) {
                log.warn("找不到namespace为[{}]的Mapper", namespace);
                return;
            }
            Collection<? extends UniversalBean<K>> sources = mapper.getSourcesByIds(keys);
            sources.forEach(s -> sourceTable.put(namespace, s.getId(), s));
        });
        return sourceTable;
    }

}
