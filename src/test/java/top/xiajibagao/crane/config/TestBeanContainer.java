package top.xiajibagao.crane.config;

import org.springframework.stereotype.Component;
import top.xiajibagao.crane.container.Container;
import top.xiajibagao.crane.helper.BaseTableMap;
import top.xiajibagao.crane.helper.TableMap;
import top.xiajibagao.crane.parse.interfaces.AssembleOperation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author huangchengxing
 * @date 2022/03/03 13:03
 */
@Component
public class TestBeanContainer implements Container {

    @Override
    public void process(List<Object> targets, List<AssembleOperation> operations) {
        // 模拟一个服务，根据id查询返回一个bean
        TableMap<Integer, String, Object> mockService = new BaseTableMap<>();
        Map<String, Object> mockBean = new HashMap<>();
        mockBean.put("name", "小李");
        mockBean.put("age", 18);
        mockBean.put("id", 1);
        mockService.put(1, mockBean);

        for (Object target : targets) {
            operations.forEach(operation -> {
                // 根据id查询对象
                Object key = operation.getAssembler().getKey(target, operation);
                if (Objects.isNull(key)) {
                    return;
                }
                Map<String, Object> beam = mockService.get(Integer.valueOf(key.toString()));
                if (Objects.nonNull(beam)) {
                    operation.getAssembler().execute(target, beam, operation);
                }
            });
        }
    }

}
