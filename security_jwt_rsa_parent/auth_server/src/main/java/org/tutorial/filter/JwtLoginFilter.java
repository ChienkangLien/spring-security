package org.tutorial.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.tutorial.config.RsaKeyProperties;
import org.tutorial.model.PO.RolePO;
import org.tutorial.model.PO.UserPO;
import org.tutorial.utils.JwtUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {
	private AuthenticationManager authenticationManager;
	private RsaKeyProperties prop;

	public JwtLoginFilter(AuthenticationManager authenticationManager, RsaKeyProperties prop) {
		this.authenticationManager = authenticationManager;
		this.prop = prop;
	}

	// 重寫springsecurity獲取用戶名和密碼操作
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		try {
			// 從輸入流中獲取用戶名和密碼，而不是表單
			UserPO userPO = new ObjectMapper().readValue(request.getInputStream(), UserPO.class);
			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
					userPO.getUsername(), userPO.getPassword());
			return authenticationManager.authenticate(authRequest);
		} catch (Exception e) {
			try {
				// 處理失敗請求
				response.setContentType("application/json;charset=utf-8");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				PrintWriter out = response.getWriter();
				Map<String, Object> map = new HashMap<>();
				map.put("code", HttpServletResponse.SC_UNAUTHORIZED);
				map.put("msg", "用戶名或者密碼錯誤");
				out.write(new ObjectMapper().writeValueAsString(map));
				out.flush();
				out.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			throw new RuntimeException(e);
		}
	}

	// 重寫用戶名密碼授權成功操作----返回token憑證
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		// 從authResult獲取認證成功的用戶信息
		UserPO resultUser = new UserPO();
		resultUser.setUsername(authResult.getName());

		Set<RolePO> roleSet = authResult.getAuthorities().stream().map(grantedAuthority -> {
			RolePO role = new RolePO();
			role.setRoleName(grantedAuthority.getAuthority());
			return role;
		}).collect(Collectors.toSet());

		resultUser.setRolePOs(roleSet);
		String token = JwtUtils.generateTokenExpireInMinutes(resultUser, prop.getPrivateKey(), 60);
		// 將token寫入header
		response.addHeader("Authorization", "Bearer " + token);
		try {
			// 登錄成功時，返回json格式進行提示
			response.setContentType("application/json;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			PrintWriter out = response.getWriter();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("code", HttpServletResponse.SC_OK);
			map.put("message", "登陸成功！");
			out.write(new ObjectMapper().writeValueAsString(map));
			out.flush();
			out.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
