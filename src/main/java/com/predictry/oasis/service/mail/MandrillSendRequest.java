package com.predictry.oasis.service.mail;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent Json used for sending email using Mandrill REST API.
 *
 * @author jocki
 */
@SuppressWarnings("unused")
public class MandrillSendRequest {

    private String key;

    private Message message;

    public MandrillSendRequest(String key, Message message) {
        this.key = key;
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    /**
     * The "message" part of Json used by Mandrill REST send email API.
     */
    @SuppressWarnings("unused")
    static public class Message {

        private String from_email;
        private List<Recipient> to = new ArrayList<>();
        private String subject;
        private String html;

        public Message(String from_email, Recipient to, String subject, String html) {
            this.from_email = from_email;
            this.to.add(to);
            this.subject = subject;
            this.html = html;
        }

        public String getFrom_email() {
            return from_email;
        }

        public void setFrom_email(String from_email) {
            this.from_email = from_email;
        }

        public List<Recipient> getTo() {
            return to;
        }

        public void addTo(Recipient recipient) {
            to.add(recipient);
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getHtml() {
            return html;
        }

        public void setHtml(String html) {
            this.html = html;
        }
    }

    /**
     * The recipient part of Json used by Mandrill REST send email API.
     */
    @SuppressWarnings("unused")
    static public class Recipient {

        private String email;

        public Recipient(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

    }

}
