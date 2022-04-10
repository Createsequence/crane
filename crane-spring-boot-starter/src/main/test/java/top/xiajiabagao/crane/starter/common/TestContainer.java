package top.xiajiabagao.crane.starter.common;

import lombok.Getter;
import top.xiajibagao.crane.core.container.BaseKeyContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author huangchengxing
 * @date 2022/04/10 17:03
 */
@Getter
public class TestContainer extends BaseKeyContainer<Integer> {

    private Map<Integer, Object> mockDatas = new HashMap<>();

    @Override
    protected Map<Integer, ?> getSources(Set<Integer> keys) {
        return mockDatas;
    }

}
