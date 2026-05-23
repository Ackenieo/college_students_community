package org.example.communityserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "review.fallback")
public class ReviewFallbackProperties {

    private boolean enabled = true;
    private boolean aiReviewEnabled;
    private boolean approveOnUnavailable = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAiReviewEnabled() {
        return aiReviewEnabled;
    }

    public void setAiReviewEnabled(boolean aiReviewEnabled) {
        this.aiReviewEnabled = aiReviewEnabled;
    }

    public boolean isApproveOnUnavailable() {
        return approveOnUnavailable;
    }

    public void setApproveOnUnavailable(boolean approveOnUnavailable) {
        this.approveOnUnavailable = approveOnUnavailable;
    }
}
