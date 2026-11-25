package se.yrgo.frontend.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestClient;
import se.yrgo.frontend.security.RemoteAuthenticationProvider;

@Configuration
public class SecurityConfig {

    private final RemoteAuthenticationProvider authenticationProvider;

    public SecurityConfig(
            RemoteAuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register").permitAll()
                        .requestMatchers("/home", "/css/**").permitAll()
                        .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                        .requestMatchers("/profile/addresses/**").hasRole("USER")
                        .requestMatchers("/profile", "/profile/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(302);
                            response.setHeader("Location", "/home");
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(302);
                            if (exception instanceof AuthenticationServiceException) {
                                response.setHeader("Location", "/login?unavailable");
                            } else {
                                response.setHeader("Location", "/login?error");
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(302);
                            response.setHeader("Location", "/login?logout");
                        })
                        .permitAll()
                );

        return http.build();
    }
}