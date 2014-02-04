package io.redlink.sdk.impl.search.model;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class SearchResults implements Iterator<SearchResult> {

    private int totalResults;

    private int startIndex;

    private int itemsPerPage;

    private Queue<SearchResult> results;

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public void setResults(List<SearchResult> results) {
        this.results = new LinkedBlockingQueue<SearchResult>(results);
    }

    @Override
    public boolean hasNext() {
        return !results.isEmpty();
    }

    @Override
    public SearchResult next() {
        //TODO: implement next page loading if empty?
        return results.poll();
    }

    @Override
    public void remove() {
        results.poll();
    }

}
