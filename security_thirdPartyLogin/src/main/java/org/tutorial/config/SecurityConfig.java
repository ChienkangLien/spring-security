package org.tutorial.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
		.authorizeRequests().antMatchers("/login","/oauth2/authorization/*").permitAll() // 1.自製登入頁 2.第三方驗證服務器
        .anyRequest()
        .authenticated()
        .and()
        .oauth2Login()
        .loginPage("/login") // 自製登入頁
        .defaultSuccessUrl("/", true) // 成功跳轉
        // 另可再.and().logout() 配置更多登出的操作，這邊就採用默認配置：/logout(POST) 登出後會跳轉/login?logout
        .and()
        .csrf()
        .ignoringAntMatchers("/logout")
        ;
        
	}
	
}
