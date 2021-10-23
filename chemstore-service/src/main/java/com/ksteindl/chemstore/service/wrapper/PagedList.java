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

    public PagedList( Page<T> pagedList) {
        this.content = pagedList.getContent();
        this.currentPage = pagedList.getNumber();
        this.totalItems = pagedList.getTotalElements();
        this.totalPages = pagedList.getTotalPages();
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
