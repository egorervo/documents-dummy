package com.stripo.example.documents.security;

import com.stripo.example.documents.rest.RestConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**")
                .authorizeRequests()
                .anyRequest()
                .permitAll()
                .and().httpBasic().and()
                .csrf().disable();
    }

    @Configuration
    @Order(1)
    public static class DeleteDocumentConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/" + RestConstants.BASE_URL + "**")
                    .authorizeRequests()
                    .anyRequest()
                    .hasAnyRole("USER")
                    .and().httpBasic().and()
                    .csrf().disable();
        }
    }

}
