package com.ankit.quicklink.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitFilter implements Filter {
    private final StringRedisTemplate redisTemplate;

    public RateLimitFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate=redisTemplate;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest= (HttpServletRequest) request;
        String path=httpRequest.getRequestURI();
        if(!path.matches("/links/[A-Za-z0-9]{6}")) {
            chain.doFilter(request, response);
            return;
        }
        String ip=httpRequest.getRemoteAddr();
        String key="rate_limit:"+ip;
        Long count=redisTemplate.opsForValue().increment(key);
        if (count == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(1));
        }
        if(count>12) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(429);
            httpResponse.getWriter().write("Rate-limit exceeded, please try again later.");
            return;
        }
        chain.doFilter(request, response);
    }
}
