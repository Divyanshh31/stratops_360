package com.stratops.stratops360.config;

import com.stratops.stratops360.controller.RoleContextAdvice;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Spring Security configuration for StratOps 360 MIS platform.
 * Provides authenticated accounts for Anthony's Triangle Management Levels:
 * 1. Executive (Strategic Level - ESS): executive / executive123
 * 2. Manager (Tactical Level - MIS/DSS): manager / manager123
 * 3. Operations (Operational Level - TPS): operations / operations123
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails executive = User.withUsername("executive")
                .password("{noop}executive123")
                .roles("EXECUTIVE", "USER")
                .build();

        UserDetails manager = User.withUsername("manager")
                .password("{noop}manager123")
                .roles("MANAGER", "USER")
                .build();

        UserDetails operations = User.withUsername("operations")
                .password("{noop}operations123")
                .roles("OPERATIONS", "USER")
                .build();

        return new InMemoryUserDetailsManager(executive, manager, operations);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**", "/api/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.svg", "/favicon.ico").permitAll()
                        .requestMatchers("/login", "/logout", "/role/**").permitAll()
                        .requestMatchers("/**").permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(authenticationSuccessHandler())
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "stratops_role")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**", "/api/**")
                )
                .addFilterAfter(new CsrfTokenEagerFilter(), CsrfFilter.class)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String username = authentication.getName();
            String role = "Manager";
            if ("operations".equalsIgnoreCase(username)) {
                role = "Operations";
            } else if ("executive".equalsIgnoreCase(username)) {
                role = "Executive";
            }
            request.getSession().setAttribute(RoleContextAdvice.SESSION_ROLE_KEY, role);

            Cookie cookie = new Cookie("stratops_role", role);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 30);
            cookie.setHttpOnly(false);
            response.addCookie(cookie);

            response.sendRedirect("/dashboard");
        };
    }

    /**
     * Eagerly resolves the CSRF token and initializes session cookie
     * before template rendering begins, preventing response-committed IllegalStateException.
     */
    private static class CsrfTokenEagerFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken != null) {
                csrfToken.getToken();
            }
            filterChain.doFilter(request, response);
        }
    }
}