package com.predictry.oasis.service.mail;

/**
 * This class represents response after calling Mandrill send mail REST API.
 *
 * @author jocki
 */
@SuppressWarnings("unused")
public class MandrillSendResponse {

    private String email;
    private String status;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "MandrillSendResponse{" +
                "email='" + email + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

}
