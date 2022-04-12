package top.xiajibagao.annotation;

/**
 * 表明注解对象在通过jackson序列化时，需要进行数据填充
 *
 * @author huangchengxing
 * @date 2022/04/12 17:52
 */
public @interface ProcessJacksonNode {

    /**
     * 要使用的配置解析器
     */
    Class<?> parser() default Void.class;

    /**
     * 要使用的操作者工厂
     */
    Class<?> operatorFactory() default Void.class;

    /**
     * 要使用的执行器
     */
    Class<?> executor() default Void.class;

}
