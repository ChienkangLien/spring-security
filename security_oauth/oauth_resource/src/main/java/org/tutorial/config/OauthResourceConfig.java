package org.tutorial.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(securedEnabled = true) // 啟用@Secured管理，另jsr250Enabled管理@RolesAllowed、prePostEnabled管理@PostAuthorize、proxyTargetClass管理代理對象
public class OauthResourceConfig extends ResourceServerConfigurerAdapter {

	@Autowired
	private DataSource dataSource;
	
	/**
	 * 指定token的持久化策略、其下有： RedisTokenStore保存到redis中， JdbcTokenStore保存到數據庫中，
	 * InMemoryTokenStore保存到內存中， 此範例選擇保存在資料庫中
	 */
//	@Bean
//	public TokenStore tokenStore() {
//		return new JdbcTokenStore(dataSource);
//	}
	
	/**
	 * Jwt Token，這種方式就不需要存儲在資料庫，也與JdbcTokenStore相抵觸，擇一使用
	 */
	@Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }
	
	@Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("my_signing_key"); // 對稱式加密
        return converter;
    }

	/**
	 * 指定當前資源的id和存儲方案
	 */
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId("emp_api") // 指定當前資源的id；對應oauth_client_details的resource_ids
			.tokenStore(tokenStore()); // 指定保存token的方式
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			// 指定不同請求方式訪問資源所需要的權限，一般查詢是read，其餘是write；對應oauth_client_details的scope
			.antMatchers(HttpMethod.GET, "/**").access("#oauth2.hasScope('read')")
			.antMatchers(HttpMethod.POST, "/**").access("#oauth2.hasScope('write')")
			.antMatchers(HttpMethod.PATCH, "/**").access("#oauth2.hasScope('write')")
			.antMatchers(HttpMethod.PUT, "/**").access("#oauth2.hasScope('write')")
			.antMatchers(HttpMethod.DELETE, "/**").access("#oauth2.hasScope('write')")
			.and()
			.headers().addHeaderWriter((request, response) -> {
				response.addHeader("Access-Control-Allow-Origin", "*"); // 允許跨域
				if (request.getMethod().equals("OPTIONS")) { // 如果是跨域的預檢請求，則原封不動向下傳達request header資訊
					response.setHeader("Access-Control-Allow-Methods",
							request.getHeader("Access-Control-Request-Method"));
					response.setHeader("Access-Control-Allow-Headers",
							request.getHeader("Access-Control-Request-Headers"));
				}
			});
	}
}
