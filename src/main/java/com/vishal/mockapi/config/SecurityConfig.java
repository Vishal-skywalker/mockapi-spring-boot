package com.vishal.mockapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)
            throws Exception {
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        requestCache.setMatchingRequestParameterName(null);
        return httpSecurity
                .csrf(csrf -> {
                    csrf.disable();
                })
                .cors(AbstractHttpConfigurer::disable)
                .formLogin(httpForm -> {
                    httpForm.loginPage("/login").permitAll();

                })
                .authorizeHttpRequests(register -> {
                    register.requestMatchers("/register", "/validateUser",
                            "/js/*", "/img/*",
                            "/css/*", "/error",
                            "/forgot-password", "reset-password", "/favicon.ico",
                            "/login", "/api/**").permitAll();
                    register.requestMatchers("/admin").hasAuthority("ADMIN");
                    register.anyRequest().authenticated();
                })
                .logout(logout -> logout
                        .logoutRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher("/logout"))
                        .logoutSuccessUrl("/login")
                        .permitAll())
                .requestCache((cache) -> cache
                        .requestCache(requestCache))
                .build();
    }

    // @Bean
    // public JavaMailSender getJavaMailSender() {
    // JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    // mailSender.setHost("smtp.gmail.com");
    // mailSender.setPort(587);
    //
    // mailSender.setUsername("useme8583@gmail.com");
    // mailSender.setPassword("icvr ablu cqvk hpbr");
    //
    // Properties props = mailSender.getJavaMailProperties();
    // props.put("mail.transport.protocol", "smtp");
    // props.put("mail.smtp.auth", "true");
    // props.put("mail.smtp.starttls.enable", "true");
    // props.put("mail.debug", "true");
    //
    // return mailSender;
    // }
}
