package org.tutorial.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

@Configuration
@EnableResourceServer
public class OauthResourceConfig extends ResourceServerConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	/**
	 * 指定token的持久化策略、其下有： RedisTokenStore保存到redis中， JdbcTokenStore保存到數據庫中，
	 * InMemoryTokenStore保存到內存中， 此範例選擇保存在資料庫中
	 */
	@Bean
	public TokenStore jdbcTokenStore() {
		return new JdbcTokenStore(dataSource);
	}

	/**
	 * 指定當前資源的id和存儲方案
	 */
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId("emp_api") // 指定當前資源的id
			.tokenStore(jdbcTokenStore()); // 指定保存token的方式
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			// 指定不同請求方式訪問資源所需要的權限，一般查詢是read，其餘是write
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
