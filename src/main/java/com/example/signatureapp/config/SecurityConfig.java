package com.example.signatureapp.config;

import com.example.signatureapp.security.UserRoleAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @org.springframework.beans.factory.annotation.Autowired
    private UserRoleAuthenticationFilter userRoleAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Отключаем CSRF для REST API
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(new AntPathRequestMatcher("/api/signatures/**")).permitAll() // Разрешаем доступ к API сигнатур для всех
                .anyRequest().authenticated() // Остальные запросы требуют аутентификации
            )
            .httpBasic(httpBasic -> {})  // Используем базовую HTTP аутентификацию
            .addFilterBefore(userRoleAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Добавляем фильтр проверки ролей пользователя

        return http.build();
    }
    
    // Этот метод заглушка для проверки ролей пользователя, в реальной системе нужно подключить UserDetailsService
    // Для учебного проекта мы просто проверяем роль по заголовку, но в реальной системе так делать нельзя
}
