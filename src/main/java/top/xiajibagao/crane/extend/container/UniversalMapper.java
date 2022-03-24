package top.xiajibagao.crane.extend.container;

import java.util.Collection;
import java.util.List;

/**
 * @author huangchengxing
 * @date 2022/03/24 20:38
 */
public interface UniversalMapper<K> {

    /**
     * 根据key查询
     *
     * @param keys keys
     * @return java.util.List<? extends top.xiajibagao.crane.extend.container.UniversalBean<K>>
     * @author huangchengxing
     * @date 2022/3/24 20:45
     */
    List<? extends UniversalBean<K>> getSourcesByIds(Collection<K> keys);

}
