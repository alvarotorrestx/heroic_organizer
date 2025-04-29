package com.example.heroicorganizer.model;

import java.util.List;

public class ApiResponse {
    private List<Comic> results;

    public List<Comic> getResults() {
        return results;
    }

    public void setResults(List<Comic> results) {
        this.results = results;
    }
}
