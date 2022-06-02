package top.xiajiabagao.crane.starter.example.common;

import lombok.Getter;
import top.xiajibagao.crane.core.container.BaseKeyContainer;

import javax.annotation.Nonnull;
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

    @Nonnull
    @Override
    protected Map<Integer, Object> getSources(@Nonnull Set<Integer> keys) {
        return mockDatas;
    }

}