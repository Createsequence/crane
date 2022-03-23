package top.xiajibagao.crane.config;

import org.springframework.stereotype.Component;
import top.xiajibagao.crane.Gender;
import top.xiajibagao.crane.impl.bean.aop.ProcessResult;
import top.xiajibagao.crane.model.BeanPerson;

import java.util.Arrays;
import java.util.List;

/**
 * @author huangchengxing
 * @date 2022/03/23 23:16
 */
@Component
public class TestService {

    @ProcessResult(targetClass = BeanPerson.class, condition = "#isProcess")
    public List<BeanPerson> listBeanPerson(String isProcess) {
        return Arrays.asList(
            new BeanPerson().setName("小明").setSex(1).setId(1),
            new BeanPerson().setName("小李").setGender(Gender.FEMALE)
                .setRelatives(Arrays.asList(
                    new BeanPerson().setName("小李妈").setSex(1),
                    new BeanPerson().setName("小李爸").setSex(2)
                ))
        );
    }

}
