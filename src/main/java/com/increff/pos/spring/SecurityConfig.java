package com.increff.pos.spring;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static Logger logger = LogManager.getLogger(SecurityConfig.class);

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http//
			// Match only these URLs
				.requestMatchers()//
				.antMatchers("/api/**")//
				.antMatchers("/ui/**")//
				.and().authorizeRequests()//

				//order has access of both supervisor and operator
				.antMatchers(HttpMethod.POST,"/api/order/**").hasAnyAuthority("supervisor","operator")//
				.antMatchers(HttpMethod.PUT,"/api/order/**").hasAnyAuthority("supervisor","operator")//


				.antMatchers(HttpMethod.POST,"/api/**").hasAuthority("supervisor")//
				.antMatchers(HttpMethod.PUT,"/api/**").hasAuthority("supervisor")//

				//reports are accessed only by supervisor
				.antMatchers(HttpMethod.GET,"/api/report/**").hasAuthority("supervisor")//
				.antMatchers(HttpMethod.POST,"/api/report/**").hasAuthority("supervisor")//

				.antMatchers("/api/**").hasAnyAuthority("supervisor", "operator")//
				.antMatchers("/ui/**").hasAnyAuthority("supervisor", "operator")//
				.and()
				.formLogin()
				.loginPage("/site/login")


				// Ignore CSRF and CORS used for stopping cross region requests
				.and().csrf().disable().cors().disable();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security",
				"/swagger-ui.html", "/webjars/**");
	}

}
