package top.xiajibagao.crane.core.helper;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.NonNull;
import top.xiajibagao.crane.core.helper.reflex.ReflexUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 枚举字典，用于将枚举适配为字典项，并提供根据类型与名称的查询功能
 *
 * @author huangchengxing
 * @date 2022/01/07 13:59
 */
@Slf4j
public class EnumDict {

    private final Map<String, EnumDictType<?>> nameCache = new HashMap<>();
    private final Map<Class<?>, EnumDictType<?>> classCache = new HashMap<>();

    private static final EnumDict INSTANCE = new EnumDict();
    
    /**
     * 获取默认字典单例
     *
     * @return cn.net.nova.dict.common.component.core.parse.EnumDict
     * @author huangchengxing
     * @date 2022/1/25 15:05
     */
    public static EnumDict instance() {
        return INSTANCE;
    }

    // ================================ register ================================

    /**
     * 添加一组枚举，类型名称为枚举类名，字典项枚举实例名称，当枚举类上存在{@link Item}注解时，将优先使用注解指定的配置
     *
     * @param targetType 目标类型
     * @author huangchengxing
     * @date 2022/1/7 15:05
     */
    public <T extends Enum<?>> void register(Class<T> targetType) {
        register(targetType, null, null);
    }

    /**
     * 注销已注册的枚举
     *
     * @param targetType 目标类型
     * @author huangchengxing
     * @date 2022/1/7 8:05
     * @since 0.5.4
     */
    public <T extends Enum<?>> void unregister(Class<T> targetType) {
        Optional.ofNullable(classCache.remove(targetType))
            .ifPresent(t -> nameCache.remove(t.getName()));
    }

    /**
     * 注销已注册的枚举
     *
     * @param targetTypeName 目标类型名称
     * @author huangchengxing
     * @date 2022/1/7 8:05
     * @since 0.5.4
     */
    public void unregister(String targetTypeName) {
        Optional.ofNullable(nameCache.remove(targetTypeName))
            .ifPresent(t -> classCache.remove(t.getType()));
    }

    /**
     * 添加一组枚举，并指定类型名称和字典项名称，当枚举类上存在{@link Item}注解时，将优先使用注解指定的配置
     *
     * @param targetType 目标类型
     * @param typeName 类型名称，若为空则默认取枚举类名
     * @param itemNameGetter 字典项名称的获取方法，若为空则默认为枚举实例名称
     * @author huangchengxing
     * @date 2022/1/7 15:05
     */
    public <T extends Enum<?>> void register(
        Class<T> targetType, String typeName, Function<T, String> itemNameGetter) {
        // 获取类名称与枚举项名称
        Item annotation = AnnotatedElementUtils.findMergedAnnotation(targetType, Item.class);
        if (Objects.nonNull(annotation)) {
            typeName = annotation.typeName();
            itemNameGetter = t -> Optional.ofNullable(ReflexUtils.findGetterMethod(targetType, annotation.itemNameProperty()))
                .map(m -> ReflectUtil.invoke(t, m))
                .map(String::valueOf)
                .orElseThrow(() -> new IllegalArgumentException(CharSequenceUtil.format(
                    "枚举项名称为空，枚举类{}无法注册到字典"
                )));
        }
        typeName = ObjectUtils.defaultIfNull(typeName, targetType.getSimpleName());
        itemNameGetter = ObjectUtils.defaultIfNull(itemNameGetter, T::name);

        // 生成对应的枚举实例
        EnumDictType<T> type = new EnumDictType<>(targetType, typeName);
        List<EnumDictItem<T>> targets = new ArrayList<>();
        for (T item : targetType.getEnumConstants()) {
            targets.add(new EnumDictItem<>(type, item, itemNameGetter.apply(item)));
        }
        targets.forEach(type::addItem);
        log.info(
            "注册枚举[{}]({})：[{}]",
            typeName, targetType,
            targets.stream()
                .map(t -> CharSequenceUtil.format("{}->{}", t.getName(), t.getTarget().name()))
                .collect(Collectors.joining(", "))
        );

        nameCache.put(typeName, type);
        classCache.put(targetType, type);
    }

    // ================================ get ================================

    /**
     * 获取指定枚举类型
     *
     * @param typeName 字典类型名称
     * @return cn.net.nova.dict.api.dict.core.parse.EnumDict.EnumDictType<T>
     * @author huangchengxing
     * @date 2022/1/7 15:48
     */
    @SuppressWarnings("unchecked")
    public <T extends Enum<?>> EnumDictType<T> getType(String typeName) {
        return (EnumDictType<T>) nameCache.get(typeName);
    }

    /**
     * 获取指定枚举类型
     *
     * @param enumClass 字典枚举类型
     * @return cn.net.nova.dict.api.dict.core.parse.EnumDict.EnumDictType<T>
     * @author huangchengxing
     * @date 2022/1/7 15:48
     */
    @SuppressWarnings("unchecked")
    public <T extends Enum<?>> EnumDictType<T> getType(Class<T> enumClass) {
        return (EnumDictType<T>) classCache.get(enumClass);
    }

    /**
     * 获取指定枚举项
     *
     * @param typeName 类型名称
     * @param itemName 字典项名称
     * @return cn.net.nova.dict.api.dict.core.parse.EnumDict.EnumDictType<T>
     * @author huangchengxing
     * @date 2022/1/7 15:48
     */
    public <T extends Enum<?>> EnumDictItem<T> getItem(String typeName, String itemName) {
        EnumDictType<T> type = getType(typeName);
        return type == null ?
            null : type.get(itemName);
    }

    /**
     * 获取指定枚举项
     *
     * @param enumClass 字典枚举类型
     * @param itemName 字典项名称
     * @return cn.net.nova.dict.api.dict.core.parse.EnumDict.EnumDictType<T>
     * @author huangchengxing
     * @date 2022/1/7 15:48
     */
    public <T extends Enum<?>> EnumDictItem<T> getItem(Class<T> enumClass, String itemName) {
        EnumDictType<T> type = getType(enumClass);
        return type == null ?
            null : type.get(itemName);
    }

    /**
     * 获取指定枚举
     *
     * @param enumClass 字典枚举类型
     * @param itemName 字典项名称
     * @return cn.net.nova.dict.api.dict.core.parse.EnumDict.EnumDictType<T>
     * @author huangchengxing
     * @date 2022/1/7 15:48
     */
    public <T extends Enum<?>> T getEnum(Class<T> enumClass, String itemName) {
        return Optional.ofNullable(getType(enumClass))
            .map(t -> t.get(itemName))
            .map(EnumDictItem::getTarget)
            .orElse(null);
    }

    /**
     * 获取指定枚举
     *
     * @param typeName 类型名称
     * @param itemName 字典项名称
     * @return cn.net.nova.dict.api.dict.core.parse.EnumDict.EnumDictType<T>
     * @author huangchengxing
     * @date 2022/1/7 15:48
     */
    @SuppressWarnings("unchecked")
    public <T extends Enum<?>> T getEnum(String typeName, String itemName) {
        return (T) Optional.ofNullable(getType(typeName))
            .map(t -> t.get(itemName))
            .map(EnumDictItem::getTarget)
            .orElse(null);
    }

    // ================================ model ================================

    /**
     * 枚举类型
     *
     * @author huangchengxing 
     * @date 2022/1/7 15:45
     */
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @Getter
    public static class EnumDictType<T extends Enum<?>> {

        @EqualsAndHashCode.Include
        private final Class<T> type;
        @EqualsAndHashCode.Include
        private final String name;

        private final Map<String, EnumDictItem<T>> nameCache;
        private final Map<Enum<?>, EnumDictItem<T>> enumCache;

        protected EnumDictType(Class<T> type, String name) {
            this.type = type;
            this.name = name;
            int len = type.getEnumConstants().length;
            nameCache = new HashMap<>(len);
            enumCache = new HashMap<>(len);
        }

        /**
         * 根据字典项名称获取字典项
         *
         * @param itemName 字典项名称
         * @return cn.net.nova.dict.common.component.core.parse.EnumDict.EnumDictItem<T>
         * @author huangchengxing
         * @date 2022/1/25 15:08
         */
        public EnumDictItem<T> get(@NonNull String itemName) {
            return nameCache.get(itemName);
        }

        /**
         * 根据字典实例获取字典项
         *
         * @param target 字典实例
         * @return cn.net.nova.dict.common.component.core.parse.EnumDict.EnumDictItem<T>
         * @author huangchengxing
         * @date 2022/1/25 15:08
         */
        public EnumDictItem<T> get(@NonNull Enum<?> target) {
            return enumCache.get(target);
        }

        public void addItem(EnumDictItem<T> item) {
            nameCache.put(item.getName(), item);
            enumCache.put(item.getTarget(), item);
        }
    }
    
    /**
     * 字典项
     *
     * @author huangchengxing
     * @date 2022/1/7 15:44
     */
    @EqualsAndHashCode(callSuper = false)
    @Getter
    public static class EnumDictItem<T extends Enum<?>> extends HashMap<String, Object> {
        private final transient EnumDictType<T> type;
        private final T target;
        private final String name;

        @SuppressWarnings("unchecked")
        protected EnumDictItem(EnumDictType<T> type, T target, String name) {
            this.type = type;
            this.name = name;
            this.target = target;

            Map<String, Object> properties = new HashMap<>((Map<String, Object>)BeanMap.create(target));
            properties.remove("declaringClass");
            putAll(properties);
        }

    }
    
    /**
     * 声明被注解的类是一个字典
     *
     * @author huangchengxing 
     * @date 2022/1/7 15:44
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Item {

        /**
         * 类型名，指定从枚举中哪个属性获得，默认为{@link Enum#name()}
         */
        String itemNameProperty();

        /**
         * 枚举类在字典中的名称，默认为类名
         */
        String typeName();

    }

}
