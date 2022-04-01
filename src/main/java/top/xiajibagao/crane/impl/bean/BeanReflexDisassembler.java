package top.xiajibagao.crane.impl.bean;

import top.xiajibagao.crane.helper.CollUtils;
import top.xiajibagao.crane.helper.PropertyUtils;
import top.xiajibagao.crane.operator.interfaces.Disassembler;
import top.xiajibagao.crane.parse.interfaces.DisassembleOperation;

import java.util.Collection;
import java.util.Collections;

/**
 * @author huangchengxing
 * @date 2022/03/02 13:29
 */
public class BeanReflexDisassembler implements Disassembler {

    @Override
    public Collection<?> execute(Object target, DisassembleOperation operation) {
        return PropertyUtils.getPropertyCache(target.getClass(), operation.getTargetProperty().getName())
            .map(pc -> pc.getValue(target))
            .map(CollUtils::adaptToCollection)
            .orElse(Collections.emptyList());
    }

}
