package com.atm.config;

import com.atm.filter.JwtTokenFilter;
import com.atm.security.JwtTokenRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@Primary
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {



    private final JwtTokenRepository jwtTokenRepository;


    private final HandlerExceptionResolver resolver;

    public WebSecurityConfig(JwtTokenRepository jwtTokenRepository,
                             @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtTokenRepository = jwtTokenRepository;
        this.resolver = resolver;
    }


//    @Bean
//    public PasswordEncoder devPasswordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .addFilterAt(new JwtTokenFilter(jwtTokenRepository, resolver), CsrfFilter.class)
                .csrf().ignoringAntMatchers("/**");
//                .and()
//                .authorizeRequests()
//                .antMatchers("/auth/login")
//                .authenticated()
//                .and()
//                .httpBasic()
//                .authenticationEntryPoint(((request, response, e) -> resolver.resolveException(request, response, null, e)));
    }

}
