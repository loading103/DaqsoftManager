package com.daqsoft.mvvmfoundation.base;

import android.view.View;

public interface MultipleLayoutChildClickListener {

    /**
     * 空数据布局子 View 被点击
     *
     * @param view 被点击的 View
     * @since v1.0.0
     */
    void onEmptyChildClick(View view);

    /**
     * 出错布局子 View 被点击
     *
     * @param view 被点击的 View
     * @since v1.0.0
     */
    void onErrorChildClick(View view);

    /**
     * 自定义状态布局布局子 View 被点击
     *
     * @param view 被点击的 View
     * @since v1.0.0
     */
    void onCustomerChildClick(View view);
}
