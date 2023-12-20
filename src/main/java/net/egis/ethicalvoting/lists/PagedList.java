package net.egis.ethicalvoting.lists;

import java.util.List;

public class PagedList {

    private final List<?> list;

    public PagedList(List<?> list) {
        this.list = list;
    }

    public int getPages(int elementsPerPage) {
        //Get the number of pages in the list using the elementsPerPage.

        if (list == null || list.isEmpty()) {
            return 0;
        }

        return (int) Math.ceil((double) list.size() / elementsPerPage);
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
