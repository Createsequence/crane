package top.xiajibagao.crane.core.operator.interfaces;

import cn.hutool.core.util.ArrayUtil;
import org.springframework.core.annotation.AnnotatedElementUtils;
import top.xiajibagao.crane.core.annotation.GroupRegister;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;

import java.util.Objects;
import java.util.Optional;

/**
 * <p>表示一个可注册的组件，当组件与组件之间存在注册和被注册关系时，
 * 仅有分组{@link #getRegisterGroups()}存在交集时才应当被允许注册。<br />
 * 当组件上存在{@link GroupRegister}注解时，将以该注解上的分组优先。
 *
 * <p>约定，该接口的实现类都不应该能够注册到与其类型相同的组件中。
 *
 * @author huangchengxing
 * @date 2022/06/27 16:34
 * @since 0.5.8
 * @see TargetWriter
 * @see TargetWriteInterceptor
 * @see SourceReader
 * @see SourceReadInterceptor
 * @see OperateHandler
 * @see OperateProcessor
 */
public interface GroupRegistrable {

    /**
     * 该分组表示用于处理非Json数据的组件
     */
    String OPERATE_GROUP_JAVA_BEAN = "JAVA_BEAN";

    /**
     * 该分组表示用于处理Json数据的组件
     */
    String OPERATE_GROUP_JSON_BEAN = "JSON_BEAN";

    /**
     * 获取所属分组，若当前类上存在{@link GroupRegister}注解，则应当优先返回{@link GroupRegister#value()}
     *
     * @return java.lang.String[]
     * @author huangchengxing
     * @date 2022/6/28 11:51
     */
    default String[] getRegisterGroups() {
        return Optional.ofNullable(AnnotatedElementUtils.findMergedAnnotation(this.getClass(), GroupRegister.class))
            .map(GroupRegister::value)
            .orElse(new String[]{ OPERATE_GROUP_JAVA_BEAN, OPERATE_GROUP_JSON_BEAN });
    }

    /**
     * 组件是否可以注册到当前组件中
     *
     * @param registrable 要注册的组件
     * @return boolean
     * @author huangchengxing
     * @date 2022/6/28 13:50
     */
    default boolean isRegistrable(GroupRegistrable registrable) {
        return Objects.nonNull(registrable)
            && ArrayUtil.containsAny(getRegisterGroups(), registrable.getRegisterGroups());
    }

}
