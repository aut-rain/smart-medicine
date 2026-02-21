package com.medical.smartmedicine.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 药品类型枚举
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum MedicineTypeEnum {

    /**
     * 西药
     */
    WESTERN(0, "西药"),

    /**
     * 中药
     */
    CHINESE(1, "中药"),

    /**
     * 中成药
     */
    PATENT(2, "中成药");

    /**
     * 类型值
     */
    private final Integer type;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 根据类型值获取枚举
     *
     * @param type 类型值
     * @return 药品类型枚举
     */
    public static MedicineTypeEnum fromType(Integer type) {
        if (type == null) {
            return WESTERN;
        }
        for (MedicineTypeEnum medicineType : values()) {
            if (medicineType.type.equals(type)) {
                return medicineType;
            }
        }
        return WESTERN;
    }

    /**
     * 根据描述获取枚举
     *
     * @param description 描述
     * @return 药品类型枚举
     */
    public static MedicineTypeEnum fromDescription(String description) {
        for (MedicineTypeEnum medicineType : values()) {
            if (medicineType.description.equals(description)) {
                return medicineType;
            }
        }
        return WESTERN;
    }

    /**
     * 根据类型码获取描述
     *
     * @param type 类型码
     * @return 描述信息
     */
    public static String getDescByType(Integer type) {
        if (type == null) {
            return null;
        }
        for (MedicineTypeEnum typeEnum : values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum.getDescription();
            }
        }
        return null;
    }
}
