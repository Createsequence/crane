package io.github.createsequence.crane.starter.starter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author huangchengxing
 * @date 2022/06/02 11:08
 */
@RequiredArgsConstructor
@Getter
public enum TestEnum2 {
    ITEM_A(1, "A"), ITEM_B(2, "B");
    private final Integer id;
    private final String desc;
}
