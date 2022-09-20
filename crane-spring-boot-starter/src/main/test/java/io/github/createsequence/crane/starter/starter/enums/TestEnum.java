package io.github.createsequence.crane.starter.starter.enums;

import io.github.createsequence.crane.core.helper.EnumDict;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author huangchengxing
 * @date 2022/06/02 11:08
 */
@EnumDict.Item(typeName = "test", itemNameProperty = "id")
@RequiredArgsConstructor
@Getter
public enum TestEnum {
    ITEM_A(1, "A"), ITEM_B(2, "B");
    private final Integer id;
    private final String desc;
}
