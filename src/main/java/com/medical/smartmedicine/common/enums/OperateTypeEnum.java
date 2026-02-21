package com.medical.smartmedicine.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作类型枚举
 * 用于浏览历史记录
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum OperateTypeEnum {

    /**
     * 搜索疾病
     */
    SEARCH_ILLNESS(1, "搜索疾病"),

    /**
     * 查看疾病详情
     */
    VIEW_ILLNESS(2, "查看疾病详情"),

    /**
     * 搜索药品
     */
    SEARCH_MEDICINE(3, "搜索药品"),

    /**
     * 查看药品详情
     */
    VIEW_MEDICINE(4, "查看药品详情"),

    /**
     * 观看视频
     */
    WATCH_VIDEO(5, "观看视频"),

    /**
     * 查看资讯
     */
    VIEW_NEWS(6, "查看资讯");

    /**
     * 操作类型值
     */
    private final Integer type;

    /**
     * 操作描述
     */
    private final String description;

    /**
     * 根据类型值获取枚举
     *
     * @param type 类型值
     * @return 操作类型枚举
     */
    public static OperateTypeEnum fromType(Integer type) {
        if (type == null) {
            return SEARCH_ILLNESS;
        }
        for (OperateTypeEnum operateType : values()) {
            if (operateType.type.equals(type)) {
                return operateType;
            }
        }
        return SEARCH_ILLNESS;
    }
}
