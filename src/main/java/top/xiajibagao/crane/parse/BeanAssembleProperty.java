package top.xiajibagao.crane.parse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.parse.interfaces.AssembleProperty;

/**
 * @author huangchengxing
 * @date 2022/03/01 16:11
 */
@Getter
@RequiredArgsConstructor
public class BeanAssembleProperty implements AssembleProperty {

    private final String reference;
    private final String resource;

}
