package com.ksteindl.chemstore.service.wrapper;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

@Data
public class PagedList<T extends Serializable> {

    private List<T> content;
    private Integer currentPage;
    private Long totalItems;
    private Integer totalPages;
    
    public static <T extends Serializable> PagedListBuilder builder(List<T> content) {
        return new PagedListBuilder<T>(content);
    }

    public PagedList(Page<T> pagedList) {
        this.content = pagedList.getContent();
        this.currentPage = pagedList.getNumber();
        this.totalItems = pagedList.getTotalElements();
        this.totalPages = pagedList.getTotalPages();
    }

    PagedList(List<T> content, Integer currentPage, Long totalItems, Integer totalPages) {
        this.content = content;
        this.currentPage = currentPage;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
    }

    @Override
    public String toString() {
        return "PagedList{" +
                "content.size()=" + content.size() +
                "currentPage=" + currentPage +
                ", totalItems=" + totalItems +
                ", totalPages=" + totalPages +
                '}';
    }
    
}
