package top.xiajibagao.crane.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * @author huangchengxing
 * @date 2022/4/4 23:04
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
	
	private Integer id;
	private String name;
	private Integer age;
	
}
