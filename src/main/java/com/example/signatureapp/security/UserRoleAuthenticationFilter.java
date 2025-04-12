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

import java.io.IOException;
import java.util.Collections;

/**
 * u0424u0438u043bu044cu0442u0440 u0434u043bu044f u043fu0440u043eu0432u0435u0440u043au0438 u0440u043eu043bu0438 u043fu043eu043bu044cu0437u043eu0432u0430u0442u0435u043bu044f u043fu043e u0437u0430u0433u043eu043bu043eu0432u043au0443 User-Id
 * u0412 u0440u0435u0430u043bu044cu043du043eu0439 u0441u0438u0441u0442u0435u043cu0435 u043du0443u0436u043du043e u0438u0441u043fu043eu043bu044cu0437u043eu0432u0430u0442u044c u043fu043eu043bu043du043eu0446u0435u043du043du0443u044e u0430u0443u0442u0435u043du0442u0438u0444u0438u043au0430u0446u0438u044e
 * u041du0430u043fu0440u0438u043cu0435u0440, JWT u0442u043eu043au0435u043du044b u0438u043bu0438 OAuth2
 */
@Component
public class UserRoleAuthenticationFilter extends OncePerRequestFilter {

    // u041fu0440u0435u0444u0438u043au0441 u0434u043bu044f u0430u0434u043cu0438u043du0438u0441u0442u0440u0430u0442u043eu0440u043eu0432 u0432 u0438u0445 ID
    private static final String ADMIN_PREFIX = "admin_";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        String userId = request.getHeader("User-Id");
        
        if (userId != null && !userId.isEmpty()) {
            // u041fu0440u043eu0432u0435u0440u044fu0435u043c, u0435u0441u043bu0438 u043fu043eu043bu044cu0437u043eu0432u0430u0442u0435u043bu044c u0430u0434u043cu0438u043du0438u0441u0442u0440u0430u0442u043eu0440 (u043fu043e u043fu0440u0435u0444u0438u043au0441u0443 ID)
            if (userId.startsWith(ADMIN_PREFIX)) {
                // u0423u0441u0442u0430u043du0430u0432u043bu0438u0432u0430u0435u043c u0430u0443u0442u0435u043du0442u0438u0444u0438u043au0430u0446u0438u044e u0441 u0440u043eu043bu044cu044e ADMIN
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                                userId, 
                                null, 
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // u0423u0441u0442u0430u043du0430u0432u043bu0438u0432u0430u0435u043c u0430u0443u0442u0435u043du0442u0438u0444u0438u043au0430u0446u0438u044e u0441 u0440u043eu043bu044cu044e USER
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                                userId, 
                                null, 
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                        );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
