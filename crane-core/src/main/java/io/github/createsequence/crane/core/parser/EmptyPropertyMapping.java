package io.github.createsequence.crane.core.parser;

import io.github.createsequence.crane.core.parser.interfaces.PropertyMapping;

/**
 * 一个空装配字段配置，应直接通过{@link #instance()}方法使用它
 *
 * @author huangchengxing
 * @date 2022/04/17 22:20
 */
public class EmptyPropertyMapping implements PropertyMapping {

    private static final EmptyPropertyMapping INSTANCE = new EmptyPropertyMapping();

    public static EmptyPropertyMapping instance() {
        return INSTANCE;
    }

    EmptyPropertyMapping() {
    }

    @Override
    public String getReference() { return ""; }
    @Override
    public boolean hasReference() { return false; }
    @Override
    public boolean hasResource() { return false; }
    @Override
    public String getExp() { return ""; }
    @Override
    public Class<?> getExpType() { return Void.class; }
    @Override
    public String getSource() {
        return "";
    }

}
