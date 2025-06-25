package com.xszx.util.db;

import java.util.List;

/**
 * 分页查询结果封装类
 *
 * @param <T> 数据类型
 */
public class PageResult<T> {
    private List<T> data; // 数据列表
    private long total; // 总记录数
    private int pageNum; // 当前页码
    private int pageSize; // 每页大小
    private int totalPages; // 总页数
    private boolean hasNext; // 是否有下一页
    private boolean hasPrevious; // 是否有上一页

    public PageResult() {
    }

    public PageResult(List<T> data, long total, int pageNum, int pageSize) {
        this.data = data;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;

        // 计算总页数
        this.totalPages = (int) Math.ceil((double) total / pageSize);

        // 计算是否有上一页和下一页
        this.hasPrevious = pageNum > 1;
        this.hasNext = pageNum < totalPages;
    }

    // Getters and Setters
    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "total=" + total +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", totalPages=" + totalPages +
                ", hasNext=" + hasNext +
                ", hasPrevious=" + hasPrevious +
                ", dataSize=" + (data != null ? data.size() : 0) +
                '}';
    }
}
