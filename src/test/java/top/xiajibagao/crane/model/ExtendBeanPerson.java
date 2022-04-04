package top.xiajibagao.crane.model;

import lombok.Data;
import lombok.experimental.Accessors;
import top.xiajibagao.crane.annotation.Assemble;
import top.xiajibagao.crane.annotation.Prop;
import top.xiajibagao.crane.extend.container.MethodContainer;

/**
 * @author huangchengxing
 * @date 2022/4/4 23:09
 */
@Accessors(chain = true)
@Data
public class ExtendBeanPerson {
	
	@Assemble(container = MethodContainer.class, namespace = "user", props = {
			@Prop(src = "name", ref = "name"),
			@Prop(src = "age", ref = "age")
	})
	private Integer id;
	private String name;
	private Integer age;
	
}
