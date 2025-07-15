package com.ryl.miniprogram.vo;

import lombok.Data;

import java.util.List;

/**
 * 分页结果封装
 */
@Data
public class PageVO<T> {
    /**
     * 当前页码
     */
    private Integer pageNum;
    
    /**
     * 每页数量
     */
    private Integer pageSize;
    
    /**
     * 总页数
     */
    private Integer totalPage;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 分页数据
     */
    private List<T> list;
} 