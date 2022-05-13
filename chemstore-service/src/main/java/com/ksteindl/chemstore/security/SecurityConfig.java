package com.ksteindl.chemstore.security;

import com.ksteindl.chemstore.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
       auth.userDetailsService(appUserService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable()
                .exceptionHandling()
                    .authenticationEntryPoint(unauthorizedHandler)
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .headers()
                    .frameOptions()
                    .sameOrigin() // to enable H2 database
                    .and()
                .authorizeRequests()
                    .antMatchers("/api/account/**").hasAuthority(
                            Authority.ACCOUNT_MANAGER)
                    .antMatchers("/api/lab-manager/**").hasAnyAuthority(
                        Authority.ACCOUNT_MANAGER,
                            Authority.LAB_MANAGER)
                    .antMatchers("/api/lab-admin/**").hasAnyAuthority(
                            Authority.ACCOUNT_MANAGER,
                            Authority.LAB_MANAGER,
                            Authority.LAB_ADMIN)
                    .antMatchers("/",
                            "/favicon.ico",
                            "/**/*.ico",
                            "/**/*.gif",
                            "/**/*.png",
                            "/**/*.svg",
                            "/**/*.jpg",
                            "/**/*.html",
                            "/**/*.css",
                            "/**/*.less",
                            "/**/*.js").permitAll()
                    .antMatchers(LOGIN_URLS).permitAll()
                    .antMatchers("/hello").permitAll()
                    .antMatchers("/chemicals").permitAll()
                    .antMatchers(H2_URL).permitAll()
                    .antMatchers("/api/**").authenticated();
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
