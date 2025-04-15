package com.example.signatureapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Collections;

@Component
public class UserRoleAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(UserRoleAuthenticationFilter.class);
    private static final String ADMIN_PREFIX = "admin_";
    
    @Value("${security.api-key:#{null}}")
    private String apiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        String userId = request.getHeader("User-Id");
        String requestApiKey = request.getHeader("X-API-Key");
        
        // Добавляем проверку API ключа для повышения безопасности
        if (apiKey != null && !apiKey.isEmpty() && (requestApiKey == null || !apiKey.equals(requestApiKey))) {
            log.warn("Неверный API ключ в запросе");
            filterChain.doFilter(request, response);
            return;
        }
        
        if (userId != null && !userId.isEmpty()) {
            // ВНИМАНИЕ: В реальном приложении такой подход недопустим!
            // Проверяем, если пользователь администратор (по префиксу ID)
            if (userId.startsWith(ADMIN_PREFIX)) {
                // Устанавливаем аутентификацию с ролью ADMIN
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                                userId, 
                                null, 
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        );
                
                log.debug("Пользователь '{}' аутентифицирован с ролью ADMIN", userId);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // Устанавливаем аутентификацию с ролью USER
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                                userId, 
                                null, 
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                        );
                
                log.debug("Пользователь '{}' аутентифицирован с ролью USER", userId);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
