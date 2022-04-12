package top.xiajibagao.crane.core.helper;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Objects;

/**
 * 键值对对象
 *
 * @author huangchengxing
 * @date 2022/02/25 14:37
 */
@Getter
public class PairEntry<K, V> implements Map.Entry<K, V> {

    @Setter
    private K key;
    private V value;
    private final PairEntry<K, V> self;

    public PairEntry(K key, V value) {
        this.key = key;
        this.value = value;
        self = this;
    }

    @Override
    public V setValue(V value) {
        V old = this.value;
        this.value = value;
        return old;
    }

    public boolean nonKey() {
        return Objects.isNull(key);
    }

    public boolean hasKey() {
        return !nonKey();
    }

    public boolean nonValue() {
        return Objects.isNull(value);
    }

    public boolean hasValue() {
        return !nonValue();
    }

    public static <K, V> PairEntry<K, V> of(K key, V val) {
        return new PairEntry<>(key, val);
    }

}
