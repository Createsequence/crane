package top.xiajibagao.crane.core.parser;

import top.xiajibagao.crane.core.parser.interfaces.AssembleProperty;

/**
 * 一个空装配字段配置
 *
 * @author huangchengxing
 * @date 2022/04/17 22:20
 */
public class EmptyAssembleProperty implements AssembleProperty {

    private static final EmptyAssembleProperty INSTANCE = new EmptyAssembleProperty();

    public static EmptyAssembleProperty instance() {
        return INSTANCE;
    }

    EmptyAssembleProperty() {
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
