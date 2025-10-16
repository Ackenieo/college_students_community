package org.example.notificationserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "notification.email")
public class NotificationEmailProperties {

    private boolean enabled;
    private String fromName;
    private String fromAddress;
    private String subjectVerifyCode;
    private String subjectResetPassword;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getSubjectVerifyCode() {
        return subjectVerifyCode;
    }

    public void setSubjectVerifyCode(String subjectVerifyCode) {
        this.subjectVerifyCode = subjectVerifyCode;
    }

    public String getSubjectResetPassword() {
        return subjectResetPassword;
    }

    public void setSubjectResetPassword(String subjectResetPassword) {
        this.subjectResetPassword = subjectResetPassword;
    }
}


