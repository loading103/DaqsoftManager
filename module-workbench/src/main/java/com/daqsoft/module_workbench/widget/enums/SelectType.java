package com.daqsoft.module_workbench.widget.enums;

public enum SelectType {
    // 不可选中，也不响应选中事件回调。（默认）
    NONE(1),

    // 单选,可以反选。
    SINGLE(2),

    // 单选,不可以反选。这种模式下，至少有一个是选中的，默认是第一个
    SINGLE_IRREVOCABLY(3),

    // 多选
    MULTI(4);

    public int value;

    SelectType(int value) {
        this.value = value;
    }

    public static SelectType get(int value) {
        switch (value) {
            case 1:
                return NONE;
            case 2:
                return SINGLE;
            case 3:
                return SINGLE_IRREVOCABLY;
            case 4:
                return MULTI;
        }
        return NONE;
    }
}
