package com.cie.hr.common.security.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.cie.hr.common.security.filter.JwtAccessDeniedHandler;
import com.cie.hr.common.security.filter.JwtAuthenticationEntryPoint;
import com.cie.hr.common.security.filter.JwtAuthorizationFilter;

import lombok.RequiredArgsConstructor;

/**
 * @author Alexis TAMBIE
 * @created 04/05/2023
 * @project hr-cie
 */
@EnableWebSecurity
@EnableAsync
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    protected final String[] PUBLIC_URLS = { "/actuator/**", "/public", "/configuration/**",
            "/authenticate", "/swagger-resources/**", "/api/authentication/sign-in/**", "/api/authentication/update/**",
            "/api/authentication/refresh-token/**", "/swagger-ui/**", "/v3/api-docs/**" };

    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Qualifier("userDetailsService")
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers(PUBLIC_URLS).permitAll())
                .exceptionHandling(auth -> auth
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers(GET, "/api/employee").hasAnyAuthority("Employé RH",
                        "Employé RH & User"))
                .authorizeHttpRequests(auth -> auth.requestMatchers(POST, "/api/employee").hasAnyAuthority("Employé RH",
                        "Employé RH & User"))
                .authorizeHttpRequests(auth -> auth.requestMatchers(PUT, "/api/employee").hasAnyAuthority("Employé RH",
                        "Employé RH & User"))
                .addFilterAfter(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .logout(auth -> auth.logoutUrl("/api/logout")
                        .logoutSuccessHandler(
                                (request, response, authentication) -> SecurityContextHolder.clearContext()));
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOrigins(java.util.Arrays.asList(
            "https://evaluation-manager-cie.dctd-cie.com",
            "http://localhost:4200",
            "http://localhost:3000",
            "http://localhost:8080"
        ));
        configuration.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.Arrays.asList("*"));
        configuration.setExposedHeaders(java.util.Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
