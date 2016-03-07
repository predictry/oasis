package com.predictry.oasis.dto.mail;

import java.util.ArrayList;
import java.util.List;

/**
 * This DTO represents Json for recommendations retrieved from S3.
 *
 */
@SuppressWarnings("unused")
public class Recommendation {

    private String algo;

    private List<String> items = new ArrayList<>();

    public String getAlgo() {
        return algo;
    }

    public void setAlgo(String algo) {
        this.algo = algo;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

}
