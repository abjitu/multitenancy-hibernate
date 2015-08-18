package com.multitenancy.entity;

import java.util.ArrayList;
import java.util.List;

public class Patterns {
    private List<Pattern> patternList = new ArrayList<>();

    private long totalPages;
    private long total;

    public List<Pattern> getPatternList() {
        return patternList;
    }

    public void setPatternList(List<Pattern> patternList) {
        this.patternList = patternList;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

}
