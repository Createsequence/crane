package top.xiajibagao.crane.core.operator.interfaces;

import cn.hutool.core.util.ArrayUtil;
import org.springframework.core.annotation.AnnotatedElementUtils;
import top.xiajibagao.crane.core.annotation.ProcessorComponent;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.operator.OperateProcessorComponentSign;

import java.util.Objects;
import java.util.Optional;

/**
 * <p>表示一个可注册到{@link OperateProcessor}，并且声明了所属分组的组件。
 * 该接口用于配合{@link ProcessorComponent}注解管理{@link OperateProcessor}构建过程中的组件依赖关系。
 *
 * <p>接口提供{@link #getRegisterGroups()}方法用于获取组件所属的分组，当类上存在{@link ProcessorComponent}注解时，
 * {@link #getRegisterGroups()}应当优先返回{@link ProcessorComponent#value()}。<br />
 * 此外，接口还提供{@link #isRegistrable(OperateProcessorComponent)}用于判断一个组件是否允许被注册到另一组件中。
 * 一般情况下，若该方法返回为{@code true}，则要求两组件{@link #getRegisterGroups()}必须存在交集。
 *
 * <p>默认提供了一个基本实现{@link OperateProcessorComponentSign}。
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
 * @see OperateProcessorComponentSign
 */
public interface OperateProcessorComponent {

    /**
     * 该分组表示用于处理非Json数据的组件
     */
    String OPERATE_GROUP_JAVA_BEAN = "JAVA_BEAN";

    /**
     * 该分组表示用于处理Json数据的组件
     */
    String OPERATE_GROUP_JSON_BEAN = "JSON_BEAN";

    /**
     * 获取所属分组，若当前类上存在{@link ProcessorComponent}注解，则应当优先返回{@link ProcessorComponent#value()}
     *
     * @return java.lang.String[]
     * @author huangchengxing
     * @date 2022/6/28 11:51
     */
    default String[] getRegisterGroups() {
        return Optional.ofNullable(AnnotatedElementUtils.findMergedAnnotation(this.getClass(), ProcessorComponent.class))
            .map(ProcessorComponent::value)
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
    default boolean isRegistrable(OperateProcessorComponent registrable) {
        return Objects.nonNull(registrable)
            && ArrayUtil.containsAny(getRegisterGroups(), registrable.getRegisterGroups());
    }

}
