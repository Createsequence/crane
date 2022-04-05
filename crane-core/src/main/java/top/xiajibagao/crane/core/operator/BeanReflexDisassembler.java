package top.xiajibagao.crane.core.operator;

import top.xiajibagao.crane.core.helper.CollUtils;
import top.xiajibagao.crane.core.helper.ReflexUtils;
import top.xiajibagao.crane.core.operator.interfaces.Disassembler;
import top.xiajibagao.crane.core.parser.interfaces.DisassembleOperation;

import java.util.Collection;
import java.util.Collections;

/**
 * @author huangchengxing
 * @date 2022/03/02 13:29
 */
public class BeanReflexDisassembler implements Disassembler {

    @Override
    public Collection<?> execute(Object target, DisassembleOperation operation) {
        return ReflexUtils.findProperty(target.getClass(), operation.getTargetProperty().getName())
            .map(pc -> pc.getValue(target))
            .map(CollUtils::adaptToCollection)
            .orElse(Collections.emptyList());
    }

}
