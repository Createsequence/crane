package io.github.createsequence.crane.core.handler;

import cn.hutool.core.lang.Assert;
import io.github.createsequence.crane.core.handler.interfaces.OperateHandler;
import io.github.createsequence.crane.core.operator.OperateProcessorComponentSign;
import io.github.createsequence.crane.core.operator.interfaces.OperateProcessor;
import io.github.createsequence.crane.core.operator.interfaces.OperateProcessorComponent;
import lombok.Getter;

/**
 * {@link OperateHandler}的基本实现
 *
 * @author huangchengxing
 * @date 2022/06/28 14:58
 * @since 0.6.0
 */
@Getter
public abstract class AbstractOperateHandler implements OperateHandler {

    protected final OperateProcessor operateProcessor;
    protected final OperateProcessorComponentSign operateProcessorComponentSign;

    protected AbstractOperateHandler(OperateProcessor operateProcessor, String... defaultRegisterGroups) {
        Assert.notNull(operateProcessor, "operateProcessor must not null");
        this.operateProcessorComponentSign = new OperateProcessorComponentSign(this.getClass(), defaultRegisterGroups);
        Assert.isTrue(
            operateProcessorComponentSign.isRegistrable(operateProcessor),
            "can not to register operation processors [{}] of different groups : {}",
            operateProcessor.getClass(), operateProcessor.getRegisterGroups()
        );
        this.operateProcessor = operateProcessor;
    }

    @Override
    public String[] getRegisterGroups() {
        return operateProcessorComponentSign.getRegisterGroups();
    }

    @Override
    public boolean isRegistrable(OperateProcessorComponent registrable) {
        return operateProcessorComponentSign.isRegistrable(registrable);
    }
}
