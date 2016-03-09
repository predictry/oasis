package com.predictry.oasis.domain;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * This entity represents an email campaign request.
 */
@SuppressWarnings("unused")
@Entity
public class EmailCampaign {

    @Id @GeneratedValue
    private Long id;

    @NotBlank
    private String campaignName;

    @NotBlank
    private String pongoUserId;

    @NotNull @ManyToOne
    private Tenant tenant;

    @ElementCollection(fetch = FetchType.EAGER) @NotEmpty
    private List<EmailCampaignTarget> targets = new ArrayList<>();

    @NotBlank
    private String mandrillAPIKey;

    @NotBlank
    private String emailFrom;

    @NotBlank
    private String emailSubject;

    @NotBlank @Lob
    private String template;

    @NotNull @Enumerated
    private EmailCampaignStatus status = EmailCampaignStatus.CREATED;

    private Integer numberOfEmail = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getPongoUserId() {
        return pongoUserId;
    }

    public void setPongoUserId(String pongoUserId) {
        this.pongoUserId = pongoUserId;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public List<EmailCampaignTarget> getTargets() {
        return targets;
    }

    public void setTargets(List<EmailCampaignTarget> targets) {
        this.targets = targets;
    }

    public void addTarget(EmailCampaignTarget target) {
        this.targets.add(target);
    }

    public void addTarget(String action, Integer day) {
        EmailCampaignTarget target = new EmailCampaignTarget();
        target.setAction(action);
        target.setDay(day);
        this.targets.add(target);
    }

    public String getMandrillAPIKey() {
        return mandrillAPIKey;
    }

    public void setMandrillAPIKey(String mandrillAPIKey) {
        this.mandrillAPIKey = mandrillAPIKey;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String from) {
        this.emailFrom = from;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String subject) {
        this.emailSubject = subject;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public EmailCampaignStatus getStatus() {
        return status;
    }

    public void setStatus(EmailCampaignStatus status) {
        this.status = status;
    }

    public Integer getNumberOfEmail() {
        return numberOfEmail;
    }

    public void setNumberOfEmail(Integer numberOfEmail) {
        this.numberOfEmail = numberOfEmail;
    }

}
