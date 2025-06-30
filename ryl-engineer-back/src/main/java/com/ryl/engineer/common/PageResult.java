package com.ryl.engineer.common;

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
    private int current;
    
    /**
     * 每页大小
     */
    private int size;
    
    /**
     * 总页数
     */
    private int pages;
    
    public PageResult() {
    }
    
    public PageResult(long total, List<T> list) {
        this.total = total;
        this.list = list;
    }
    
    public PageResult(long total, List<T> list, int current, int size) {
        this.total = total;
        this.list = list;
        this.current = current;
        this.size = size;
        this.pages = (int) (total % size == 0 ? total / size : total / size + 1);
    }
    
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
    
    public int getCurrent() {
        return current;
    }
    
    public void setCurrent(int current) {
        this.current = current;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public int getPages() {
        return pages;
    }
    
    public void setPages(int pages) {
        this.pages = pages;
    }
} 