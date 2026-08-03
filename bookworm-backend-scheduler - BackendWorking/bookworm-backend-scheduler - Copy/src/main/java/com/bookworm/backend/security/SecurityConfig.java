package com.bookworm.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Stateless JWT auth. /auth/** and Swagger are public; everything else
 * requires a valid Bearer token. NOTE for the next dev: controllers still
 * take userId as a @PathVariable rather than reading it off the
 * authenticated principal - that's the natural follow-up once this lands
 * (e.g. add a check that the token's userId matches the path userId, or
 * switch controllers to pull it from @AuthenticationPrincipal entirely).
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AppUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()

                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/categories/**",
                                "/api/v1/subcategories/**",
                                "/api/v1/genres/**",
                                "/api/v1/languages/**",
                                "/api/v1/products/**",
                                "/api/v1/stakeholders/**",
                                "/api/v1/credit-types/**",
                                "/api/v1/beneficiaries/**",
                                "/api/v1/library-packages/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/categories/**",
                                "/api/v1/subcategories/**",
                                "/api/v1/genres/**",
                                "/api/v1/languages/**",
                                "/api/v1/products/**",
                                "/api/v1/stakeholders/**",
                                "/api/v1/credit-types/**",
                                "/api/v1/beneficiaries/**",
                                "/api/v1/library-packages/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v1/categories/**",
                                "/api/v1/subcategories/**",
                                "/api/v1/genres/**",
                                "/api/v1/languages/**",
                                "/api/v1/products/**",
                                "/api/v1/stakeholders/**",
                                "/api/v1/credit-types/**",
                                "/api/v1/beneficiaries/**",
                                "/api/v1/library-packages/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/v1/beneficiaries/*/royalties/**")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated())

                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}