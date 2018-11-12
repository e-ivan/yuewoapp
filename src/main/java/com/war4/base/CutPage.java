package com.war4.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dly
 */
public class CutPage<T> implements Serializable {
    public static Integer MAX_COUNT = Integer.MAX_VALUE;

    private Integer page;
    private Integer limit;
    private Integer offset;
    private Integer dataCount;
    private Long totalCount = 0L;
    private Integer totalPage;
    private List<T> iData = new ArrayList<T>();

    public CutPage(Integer page, Integer limit) {
        this.page = page;
        this.limit = limit;
        if (page == null) {
            this.page = 0;
        }
        if (limit == null) {
            this.limit = 10;
        }
        this.offset = this.page * this.limit;
        this.dataCount = 0;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public List<T> getiData() {
        return iData;
    }

    public void setiData(List<T> iData) {
        if (iData != null) {
            this.dataCount = iData.size();
        }
        this.iData = iData;
    }

    public Integer getDataCount() {
        return dataCount;
    }

    public void setDataCount(Integer dataCount) {
        this.dataCount = dataCount;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
        try {
            int t = this.totalCount.intValue();
            totalPage = t % limit == 0 ? t / limit : t / limit + 1;
        } catch (Exception e) {
            totalPage = 0;
        }
    }

    public Integer getTotalPage() {
        return totalPage;
    }
}
