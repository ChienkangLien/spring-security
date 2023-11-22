package org.tutorial.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	@Autowired
	private UserDetailsService userDetailsService;

	@Bean
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http.csrf().disable() //關閉csrf(跨站請求偽造)防護
				.authorizeRequests() //所有資源必須授權後訪問
				.anyRequest().authenticated()
				.and()
				.formLogin()
				.loginProcessingUrl("/login") //指定認證頁面可以匿名訪問
				.permitAll();
		return http.build();
	}
	
	//AuthenticationManager對象在OAuth2認證服務中要使用，放入IOC容器中
	@Bean
	public AuthenticationManager authenticationManager() {
	    return new ProviderManager(Arrays.asList(authenticationProvider(userDetailsService, passwordEncoder())));
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); //加鹽加密
	}

	public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}
}
