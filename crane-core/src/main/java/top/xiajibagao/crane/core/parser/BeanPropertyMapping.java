package top.xiajibagao.crane.core.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;

/**
 * {@link PropertyMapping}的通用实现
 *
 * @author huangchengxing
 * @date 2022/03/01 16:11
 */
@Getter
@RequiredArgsConstructor
public class BeanPropertyMapping implements PropertyMapping {

    private final String reference;
    private final String source;
    private final String exp;
    private final Class<?> expType;

    public static BeanPropertyMapping ofNameOnlyProperty(String propertyName) {
        return new BeanPropertyMapping(null, propertyName, "", Void.class);
    }

}
