package org.example.userserver.service;

import org.example.userserver.config.VerifyCodeProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
public class VerifyCodeService {

    private static final Logger logger = LoggerFactory.getLogger(VerifyCodeService.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private VerifyCodeProperties properties;

    private final SecureRandom random = new SecureRandom();

    public String generateAndStoreCode(String email, Duration ttl) {
        String key = buildKey(email);
        // 频控：距离上次发送间隔不足则拒绝
        Long expire = stringRedisTemplate.getExpire(key);
        if (expire != null && expire > (ttl.getSeconds() - properties.getPerEmailMinIntervalSeconds())) {
            throw new IllegalStateException("请求过于频繁，请稍后再试");
        }
        String code = generateCode();
        stringRedisTemplate.opsForValue().set(key, code, ttl);
        logger.info("生成验证码成功, email={}", email);
        return code;
    }

    public boolean verifyCode(String email, String code) {
        String key = buildKey(email);
        String value = stringRedisTemplate.opsForValue().get(key);
        return value != null && value.equals(code);
    }

    private String buildKey(String email) {
        return "verify:email:" + email;
    }

    private String generateCode() {
        int length = properties.getLength();
        boolean digitsOnly = properties.isDigitsOnly();
        String digits = "0123456789";
        String alnum = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        String dict = digitsOnly ? digits : alnum;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(dict.charAt(random.nextInt(dict.length())));
        }
        return sb.toString();
    }
}


