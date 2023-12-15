package net.egis.ethicalvoting.lists;

import java.util.List;

public class PagedList {

    private final List<?> list;

    public PagedList(List<?> list) {
        this.list = list;
    }

    public List<?> getPage(int page, int elementsPerPage) {
        //Split a list into pages using the current page and elementsPerPage.

        int fromIndex = page * elementsPerPage;
        if (list == null || list.size() < fromIndex) {
            return null;
        }

        // toIndex exclusive
        return list.subList(fromIndex, Math.min(fromIndex + elementsPerPage, list.size()));
    }

}
