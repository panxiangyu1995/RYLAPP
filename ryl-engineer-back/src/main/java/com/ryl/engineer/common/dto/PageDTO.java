package com.ryl.engineer.common.dto;

import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO<T> {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 当前页码
     */
    private Integer current;

    /**
     * 每页数量
     */
    private Integer size;

    /**
     * 数据列表
     */
    private List<T> list;

    /**
     * 从PageInfo转换为PageDTO
     *
     * @param pageInfo PageInfo对象
     * @param <T>      数据类型
     * @return PageDTO
     */
    public static <T> PageDTO<T> fromPageInfo(PageInfo<T> pageInfo) {
        PageDTO<T> pageDTO = new PageDTO<>();
        pageDTO.setTotal(pageInfo.getTotal());
        pageDTO.setPages(pageInfo.getPages());
        pageDTO.setCurrent(pageInfo.getPageNum());
        pageDTO.setSize(pageInfo.getPageSize());
        pageDTO.setList(pageInfo.getList());
        return pageDTO;
    }
} 