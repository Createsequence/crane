package top.xiajiabagao.crane.starter.example.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author huangchengxing
 * @date 2022/02/26 17:48
 */
@Getter
@RequiredArgsConstructor
public enum Gender {

    MALE(1, "男"),
    FEMALE(2, "女");

    private final Integer id ;
    private final String name;

}
