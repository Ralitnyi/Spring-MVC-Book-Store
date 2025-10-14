package com.epam.rd.autocode.spring.project.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final BlockedUserInterceptor blockedUserInterceptor;

    SecurityConfig(BlockedUserInterceptor blockedUserInterceptor) {
        this.blockedUserInterceptor = blockedUserInterceptor;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/client/register", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/employee/**").hasAnyRole("EMPLOYEE", "ADMIN")
                .requestMatchers("/client/**").hasAnyRole("CLIENT", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/client/login").permitAll()
                .loginProcessingUrl("/do-login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/", true)
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .csrf(Customizer.withDefaults())
            .csrf(csrf -> csrf
        		.ignoringRequestMatchers("/api/**")
            )
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}