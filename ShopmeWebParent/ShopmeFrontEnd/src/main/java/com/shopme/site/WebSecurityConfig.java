/*
 * Copyright (c) 2022.
 * Joseph Rumpe (jrumpe@gmail.com)
 * All rights reserved.
 */

package com.shopme.site;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    public void configure (HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().permitAll();

    }

    @Override
    public void configure (WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
    }
}
