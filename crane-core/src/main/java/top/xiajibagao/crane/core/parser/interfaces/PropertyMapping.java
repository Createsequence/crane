package top.xiajibagao.crane.core.parser.interfaces;

import cn.hutool.core.text.CharSequenceUtil;

/**
 * 字段映射配置
 * <p>表明在一次操作中，数据源的字段与待处理对象字段的映射关系。 <br />
 * 比如：在一次操作中，需要将数据源对象A的name字段赋值给待处理对象B的userName字段，
 * 则称name为数据源字段，对应的字段值称为数据源，而userName为引用字段。
 *
 * <p>数据源字段允许存在下述情况：
 * <ul>
 *     <li>当数据源字段不存在时：应当直接将数据源对象作为数据源；</li>
 *     <li>当数据源字段存在，且数据源作为单个对象时：应当取数据源字段值作为数据源；</li>
 *     <li>当数据源字段存在，且数据源作为复数个对象时：应当取每个对象数据源字段值作为并组为集合，然后将该集合作为数据源；</li>
 * </ul>
 * 引用字段允许存在下述情况：
 * <ul>
 *     <li>当引用字段不存在时：应当尝试将本次操作对应的key字段作为引用字段；</li>
 *     <li>当引用字段存在，且当前待处理对象作为单个对象时：应尝试将数据源填入对象对应的引用字段；</li>
 *     <li>当引用字段存在，且当前待处理对象作为复数对象时：应尝试将数据源填入集合中每一个对象对应的引用字段；</li>
 * </ul>
 *
 * <p>一般情况下，当获取到数据源字段的值后，若支持使用表达式处理，则该值应当先被表达式处理后，
 * 转为指定的表达式返回值类型，并重新以该值作为数据源，然后装配到待处理对象的指定字段。
 *
 * @see AssembleOperation
 * @author huangchengxing
 * @date 2022/03/01 16:00
 */
public interface PropertyMapping {

    /**
     * 获取引用字段
     *
     * @return java.lang.String
     * @author huangchengxing
     * @date 2022/3/3 13:20
     */
    String getReference();

    /**
     * 是否存在引用字段
     *
     * @return boolean
     * @author huangchengxing
     * @date 2022/4/8 16:49
     */
    default boolean hasReference() {
        return CharSequenceUtil.isNotBlank(getReference());
    }

    /**
     * 获取数据源字段
     *
     * @since 0.5.4
     * @return java.lang.String
     * @author huangchengxing
     * @date 2022/3/3 13:20
     */
    String getSource();

    /**
     * 是否存在数据源字段
     *
     * @return boolean
     * @author huangchengxing
     * @date 2022/4/8 16:49
     */
    default boolean hasResource() {
        return CharSequenceUtil.isNotBlank(getSource());
    }

    /**
     * 表达式
     *
     * @return java.lang.String
     * @author huangchengxing
     * @date 2022/4/13 0:08
     */
    String getExp();

    /**
     * 表达式返回值类型
     *
     * @return java.lang.Class<?>
     * @author huangchengxing
     * @date 2022/4/13 0:08
     */
    Class<?> getExpType();

}
