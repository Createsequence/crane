package top.xiajibagao.crane.core.handler;

import cn.hutool.core.lang.Assert;
import lombok.Getter;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.operator.OperateProcessorComponentSign;
import top.xiajibagao.crane.core.operator.interfaces.OperateProcessor;
import top.xiajibagao.crane.core.operator.interfaces.OperateProcessorComponent;

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
