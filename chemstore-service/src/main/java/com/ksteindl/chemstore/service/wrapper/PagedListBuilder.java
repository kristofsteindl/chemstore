package com.ksteindl.chemstore.service.wrapper;

import java.io.Serializable;
import java.util.List;

public class PagedListBuilder<T extends Serializable> {
    private List<T> content;
    private Integer currentPage;
    private Long totalItems;
    private Integer totalPages;

    public PagedListBuilder(List<T> content) {
        this.content = content;
    }

    public PagedListBuilder setContent(List<T> content) {
        this.content = content;
        return this;
    }

    public PagedListBuilder setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public PagedListBuilder setTotalItems(Long totalItems) {
        this.totalItems = totalItems;
        return this;
    }

    public PagedListBuilder setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    public PagedList build() {
        return new PagedList(content, currentPage, totalItems, totalPages);
    }
}
