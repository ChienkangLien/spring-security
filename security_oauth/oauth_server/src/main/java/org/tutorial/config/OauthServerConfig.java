package org.tutorial.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

@Configuration
@EnableAuthorizationServer
public class OauthServerConfig extends AuthorizationServerConfigurerAdapter {

	// 資料庫連接池對象
	@Autowired
	private DataSource dataSource;

	// 認證業務對象
	@Autowired
	private UserDetailsService userDetailsService;

	// 授權模式專用對象
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// 從資料庫中查詢出客戶端資料
	@Bean
	public JdbcClientDetailsService clientDetailsService() {
		JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
		jdbcClientDetailsService.setPasswordEncoder(passwordEncoder);
		return jdbcClientDetailsService;
	}

	// token保存策略
	@Bean
	public TokenStore tokenStore() {
		return new JdbcTokenStore(dataSource);
	}

	// 授權信息保存策略
	@Bean
	public ApprovalStore approvalStore() {
		return new JdbcApprovalStore(dataSource);
	}

	// 授權碼模式資料來源
	@Bean
	public AuthorizationCodeServices authorizationCodeServices() {
		return new JdbcAuthorizationCodeServices(dataSource);
	}

	// 指定客戶端登錄資料來源
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		// 從資料庫取數據
		clients.withClientDetails(clientDetailsService());

		// 從內存中取數據
//        clients.inMemory()
//                .withClient("client_one")
//                .secret(passwordEncoder.encode("password"))
//                .resourceIds("emp_api")
//                .authorizedGrantTypes(
//                        "authorization_code",
//                        "password",
//                        "client_credentials",
//                        "implicit",
//                        "refresh_token"
//                )// 該client允許的授權類型 authorization_code,password,refresh_token,implicit,client_credentials
//                .scopes("read", "write")// 允許的授權範圍
//                .autoApprove(false)
//                //加上驗證回調地址
//                .redirectUris("http://example");
	}

	// 檢測token的策略
	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.allowFormAuthenticationForClients() // 允許form表單客戶端認證，允許客戶端使用client_id和client_secret獲取token
				.checkTokenAccess("isAuthenticated()") // 檢驗token前提要經過認證
				;
	}

	// OAuth2的主配置信息
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
			.approvalStore(approvalStore())
			.authenticationManager(authenticationManager)
			.authorizationCodeServices(authorizationCodeServices())
			.tokenStore(tokenStore())
			.userDetailsService(userDetailsService);
	}
}
