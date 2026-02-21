package com.medical.smartmedicine.common.constant;

/**
 * 历史记录类型常量
 *
 * @author ZZY
 */
public class HistoryTypeConstant {

    /**
     * 用户操作类型：搜索
     */
    public static final Integer HISTORY_TYPE_SEARCH = 1;

    /**
     * 用户操作类型：查看某种疾病
     */
    public static final Integer HISTORY_TYPE_ILLNESS = 2;

    /**
     * 用户操作类型：查看相关的药
     */
    public static final Integer HISTORY_TYPE_MEDICINE = 3;

    private HistoryTypeConstant() {
        // 防止实例化
    }
}
