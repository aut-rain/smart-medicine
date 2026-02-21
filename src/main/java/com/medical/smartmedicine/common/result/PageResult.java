package com.medical.smartmedicine.common.result;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页结果封装
 * 统一分页返回格式
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页结果")
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    @Schema(description = "总记录数")
    private Long total;

    /**
     * 总页数
     */
    @Schema(description = "总页数")
    private Long pages;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码")
    private Long current;

    /**
     * 每页数量
     */
    @Schema(description = "每页数量")
    private Long size;

    /**
     * 数据列表
     */
    @Schema(description = "数据列表")
    private List<T> records;

    /**
     * 从MyBatis Plus的Page对象转换(数据类型相同)
     *
     * @param page MyBatis Plus分页对象
     * @param <T>  数据类型
     * @return PageResult对象
     */
    public static <T> PageResult<T> of(Page<T> page) {
        return PageResult.<T>builder()
                .total(page.getTotal())
                .pages(page.getPages())
                .current(page.getCurrent())
                .size(page.getSize())
                .records(page.getRecords())
                .build();
    }

    /**
     * 从MyBatis Plus的Page对象转换(需要类型转换)
     *
     * @param page      MyBatis Plus分页对象
     * @param converter 转换函数
     * @param <S>       源数据类型
     * @param <T>       目标数据类型
     * @return PageResult对象
     */
    public static <S, T> PageResult<T> of(Page<S> page, Function<S, T> converter) {
        List<T> records = page.getRecords().stream()
                .map(converter)
                .collect(Collectors.toList());

        return PageResult.<T>builder()
                .total(page.getTotal())
                .pages(page.getPages())
                .current(page.getCurrent())
                .size(page.getSize())
                .records(records)
                .build();
    }

    /**
     * 创建空分页结果
     *
     * @param <T> 数据类型
     * @return 空的PageResult对象
     */
    public static <T> PageResult<T> empty() {
        return PageResult.<T>builder()
                .total(0L)
                .pages(0L)
                .current(1L)
                .size(10L)
                .records(List.of())
                .build();
    }

    /**
     * 创建空分页结果(指定页码和大小)
     *
     * @param current 当前页码
     * @param size    每页数量
     * @param <T>     数据类型
     * @return 空的PageResult对象
     */
    public static <T> PageResult<T> empty(Long current, Long size) {
        return PageResult.<T>builder()
                .total(0L)
                .pages(0L)
                .current(current)
                .size(size)
                .records(List.of())
                .build();
    }
}
