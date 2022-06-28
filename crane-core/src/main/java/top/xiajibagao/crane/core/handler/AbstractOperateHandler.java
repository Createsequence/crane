package top.xiajibagao.crane.core.handler;

import cn.hutool.core.lang.Assert;
import lombok.Getter;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.operator.GroupRegisteredSign;
import top.xiajibagao.crane.core.operator.interfaces.GroupRegistrable;
import top.xiajibagao.crane.core.operator.interfaces.OperateProcessor;

/**
 * {@link OperateHandler}的基本实现
 *
 * @author huangchengxing
 * @date 2022/06/28 14:58
 * @since 0.5.8
 */
@Getter
public abstract class AbstractOperateHandler implements OperateHandler {

    protected final OperateProcessor operateProcessor;
    protected final GroupRegisteredSign groupRegisteredSign;

    protected AbstractOperateHandler(OperateProcessor operateProcessor, String... defaultRegisterGroups) {
        Assert.notNull(operateProcessor, "operateProcessor must not null");
        this.groupRegisteredSign = new GroupRegisteredSign(this.getClass(), defaultRegisterGroups);
        Assert.isTrue(
            groupRegisteredSign.isRegistrable(operateProcessor),
            "can not to register operation processors [{}] of different groups : [{}]",
            operateProcessor.getClass(), operateProcessor.getRegisterGroups()
        );
        this.operateProcessor = operateProcessor;
    }

    @Override
    public String[] getRegisterGroups() {
        return groupRegisteredSign.getRegisterGroups();
    }

    @Override
    public boolean isRegistrable(GroupRegistrable registrable) {
        return groupRegisteredSign.isRegistrable(registrable);
    }
}
