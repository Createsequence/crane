package top.xiajibagao.crane.core.parser.interfaces;

import cn.hutool.core.text.CharSequenceUtil;

/**
 * @author huangchengxing
 * @date 2022/03/01 16:00
 */
public interface AssembleProperty {

    /**
     * 获取一个空对象
     *
     * @return top.xiajibagao.crane.core.parser.interfaces.AssembleProperty
     * @author huangchengxing
     * @date 2022/4/8 21:12
     */
    static AssembleProperty empty() {
        return new AssembleProperty() {
            @Override
            public String getReference() {
                return "";
            }
            @Override
            public boolean hasReference() {
                return false;
            }
            @Override
            public boolean hasResource() {
                return false;
            }

            @Override
            public String getResource() {
                return "";
            }
        };
    }

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
     * @return java.lang.String
     * @author huangchengxing
     * @date 2022/3/3 13:20
     */
    String getResource();

    /**
     * 是否存在数据源字段
     *
     * @return boolean
     * @author huangchengxing
     * @date 2022/4/8 16:49
     */
    default boolean hasResource() {
        return CharSequenceUtil.isNotBlank(getResource());
    }

}
