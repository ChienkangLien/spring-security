package org.tutorial.filter;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.tutorial.config.RsaKeyProperties;
import org.tutorial.domain.Payload;
import org.tutorial.model.PO.RolePO;
import org.tutorial.model.PO.UserPO;
import org.tutorial.utils.JwtUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtVerifyFilter extends BasicAuthenticationFilter {
	private RsaKeyProperties prop;

	public JwtVerifyFilter(AuthenticationManager authenticationManager, RsaKeyProperties prop) {
		super(authenticationManager);
		this.prop = prop;
	}

	/**
	 * 過濾請求
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
		try {
			// 請求體的頭中是否包含Authorization
			String header = request.getHeader("Authorization");
			// Authorization中是否包含Bearer，不包含直接返回
			if (header == null || !header.startsWith("Bearer ")) {
				chain.doFilter(request, response);
				responseJson(response);
				return;
			}
			// 獲取權限失敗，會拋出異常
			UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
			// 獲取後，將Authentication寫入SecurityContextHolder中供後續使用
			SecurityContextHolder.getContext().setAuthentication(authentication);
			chain.doFilter(request, response);
		} catch (Exception e) {
			responseJson(response);
			e.printStackTrace();
		}
	}

	/**
	 * 未登錄提示
	 *
	 * @param response
	 */
	private void responseJson(HttpServletResponse response) {
		try {
			// 未登錄提示
			response.setContentType("application/json;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			PrintWriter out = response.getWriter();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("code", HttpServletResponse.SC_FORBIDDEN);
			map.put("message", "請登錄！");
			out.write(new ObjectMapper().writeValueAsString(map));
			out.flush();
			out.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 通過token，獲取用戶信息
	 *
	 * @param request
	 * @return
	 */
	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (token != null) {
			// 通過token解析出載荷信息
			Payload<UserPO> payload = JwtUtils.getInfoFromToken(token.replace("Bearer ", ""), prop.getPublicKey(),
					UserPO.class);
			UserPO user = payload.getUserInfo();
			// 不為null，返回
			if (user != null) {
				List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
				for (RolePO role : user.getRolePOs()) {
					authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
				}
				return new UsernamePasswordAuthenticationToken(user, null, authorities);
			}
			return null;
		}
		return null;
	}
}
