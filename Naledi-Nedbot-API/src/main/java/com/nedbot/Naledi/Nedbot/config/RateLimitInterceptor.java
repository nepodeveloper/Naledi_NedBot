package com.nedbot.Naledi.Nedbot.config;

import com.nedbot.Naledi.Nedbot.exception.RateLimitExceededException;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class RateLimitInterceptor implements HandlerInterceptor {
    private final RateLimitConfig rateLimitConfig;

    public RateLimitInterceptor(RateLimitConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String apiKey = request.getHeader("X-API-KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        Bucket bucket = rateLimitConfig.resolveBucket(apiKey);
        if (!bucket.tryConsume(1)) {
            throw new RateLimitExceededException("Rate limit exceeded. Try again later.");
        }

        return true;
    }
}