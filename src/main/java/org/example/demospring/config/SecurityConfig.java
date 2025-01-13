package org.example.demospring.config;

import org.example.demospring.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Конфигурация Spring Security для Spring Boot 3 / Spring Security 6.
 */
@Configuration
public class SecurityConfig {

    // Через конструкторную инъекцию избавляемся от возможных циклических зависимостей
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Бин провайдера аутентификации DAO,
     * который использует CustomUserDetailsService и шифрование паролей.
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * BCrypt для хеширования паролей.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Настраиваем AuthenticationManager через HttpSecurity и AuthenticationManagerBuilder.
     * Этот бин нужен, если вам где-то требуется вручную получить AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(daoAuthenticationProvider())
                .build();
    }

    /**
     * Основная настройка фильтров Spring Security (SecurityFilterChain).
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // Разрешаем доступ к руту, регистрации и статике без аутентификации
                        .requestMatchers("/", "/register", "/do-register", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                // Форма логина
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/do-login")
                        .defaultSuccessUrl("/users", true) // Теперь после успешного логина идём на /users
                        .permitAll()
                )
                // Логаут
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                // Защиту от CSRF включаем по умолчанию
                .csrf(Customizer.withDefaults());

        return http.build();
    }

}
