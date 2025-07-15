package com.ryl.engineer.common;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 分页结果封装类
 * @param <T> 数据类型
 */
public class PageResult<T> {
    /**
     * 总记录数
     */
    private long total;

    /**
     * 数据列表
     */
    private List<T> list;

    /**
     * 当前页码
     */
    private long current;

    /**
     * 每页大小
     */
    private long size;

    /**
     * 总页数
     */
    private long pages;

    public PageResult() {
    }

    public PageResult(List<T> list, long total, long size, long current) {
        this.list = list;
        this.total = total;
        this.size = size;
        this.current = current;
        this.pages = (total + size - 1) / size;
    }

    /**
     * 从MyBatis-Plus的IPage对象创建PageResult
     * @param page IPage对象
     * @param <T>  数据类型
     * @return PageResult
     */
    public static <T> PageResult<T> fromPage(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(page.getTotal());
        result.setList(page.getRecords());
        result.setCurrent(page.getCurrent());
        result.setSize(page.getSize());
        result.setPages(page.getPages());
        return result;
    }

    // Getters and Setters
    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }
} 