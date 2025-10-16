package org.example.userserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "verify-code")
public class VerifyCodeProperties {
    private int perEmailMinIntervalSeconds;
    private int length;
    private boolean digitsOnly;

    public int getPerEmailMinIntervalSeconds() { return perEmailMinIntervalSeconds; }
    public void setPerEmailMinIntervalSeconds(int perEmailMinIntervalSeconds) { this.perEmailMinIntervalSeconds = perEmailMinIntervalSeconds; }
    public int getLength() { return length; }
    public void setLength(int length) { this.length = length; }
    public boolean isDigitsOnly() { return digitsOnly; }
    public void setDigitsOnly(boolean digitsOnly) { this.digitsOnly = digitsOnly; }
}


