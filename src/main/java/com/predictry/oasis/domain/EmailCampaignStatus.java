package com.predictry.oasis.domain;

/**
 * Represent status of an <code>EmailCampaign</code>.
 */
@SuppressWarnings("unused")
public enum EmailCampaignStatus {

    CREATED("created"), DONE("done"), ERROR("error");

    private String description;

    EmailCampaignStatus(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

}
