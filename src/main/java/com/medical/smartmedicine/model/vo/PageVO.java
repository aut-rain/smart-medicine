package com.medical.smartmedicine.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果VO
 * 
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageVO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<T> records;

    private Long total;

    private Integer current;

    private Integer size;

    private Integer pages;

    /**
     * 构建分页结果
     */
    public static <T> PageVO<T> build(List<T> records, Long total, Integer current, Integer size) {
        int pages = (int) Math.ceil((double) total / size);
        return PageVO.<T>builder()
                .records(records)
                .total(total)
                .current(current)
                .size(size)
                .pages(pages)
                .build();
    }
}
