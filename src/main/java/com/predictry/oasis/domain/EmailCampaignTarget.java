package com.predictry.oasis.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Embeddable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * This class represent email campaign's target.
 */
@Embeddable
public class EmailCampaignTarget {

    @NotBlank
    private String action;

    @NotNull @Min(0L)
    private Integer day = 0;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public LocalDate targetDate() {
        return LocalDate.now().minusDays(day);
    }

}
