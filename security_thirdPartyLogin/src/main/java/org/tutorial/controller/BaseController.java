package org.tutorial.controller;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class BaseController {

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/")
	public String success(Model model, Authentication authentication) {
		if (authentication != null && authentication.isAuthenticated()) {
			// 獲取用戶身分資訊
			Object principal = authentication.getPrincipal();
			if (principal instanceof OAuth2User) {
				OAuth2User oAuth2User = (OAuth2User) principal;
				Map<String, Object> userAttributes = oAuth2User.getAttributes();
				Collection<? extends GrantedAuthority> userAuthorities = oAuth2User.getAuthorities();
				
				// 身分資訊根據不同的認證服務器或者授權服務器返回，內容不盡相同，傳到前端印出看
				model.addAttribute("userAttributes", userAttributes);
				model.addAttribute("userAuthorities", userAuthorities); // 至少都有ROLE_USER
			}
			
		}
		return "index";
	}
}
