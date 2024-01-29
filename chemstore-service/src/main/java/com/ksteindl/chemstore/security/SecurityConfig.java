package com.ksteindl.chemstore.security;

import com.ksteindl.chemstore.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig {

    public static final String LOGIN_URLS = "/api/login";
    public static final String H2_URL = "/h2-console/**";

    @Autowired
    private UnauthorizedHandler unauthorizedHandler;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.eraseCredentials(false);
//        auth.userDetailsService(appUserService).passwordEncoder(bCryptPasswordEncoder);
//    }


    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .exceptionHandling(customizer -> customizer.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api/account/**").hasAuthority(Authority.ACCOUNT_MANAGER)
                        .requestMatchers("/api/lab-manager/**").hasAnyAuthority(Authority.ACCOUNT_MANAGER, Authority.LAB_MANAGER)
                        .requestMatchers("/api/lab-admin/**").hasAnyAuthority(Authority.ACCOUNT_MANAGER, Authority.LAB_MANAGER, Authority.LAB_ADMIN)
                        .requestMatchers(
                               "/",
                               "/favicon.ico",
                               "/**.ico",
                               "/**.gif",
                               "/**.png",
                               "/**.svg",
                               "/**.jpg",
                               "/**.html",
                               "/**.css",
                               "/**.less",
                               "/**.js").permitAll()
                        .requestMatchers(LOGIN_URLS).permitAll()
                        .requestMatchers("/hello").permitAll()
                        .requestMatchers("/chemicals").permitAll()
                        .requestMatchers(H2_URL).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
