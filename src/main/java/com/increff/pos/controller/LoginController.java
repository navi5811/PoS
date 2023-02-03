package com.increff.pos.controller;

import java.util.ArrayList;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.increff.pos.dto.UserDto;
import com.increff.pos.model.UserData;
import com.increff.pos.model.InfoData;
import com.increff.pos.model.LoginForm;
import com.increff.pos.service.ApiException;
import com.increff.pos.util.SecurityUtil;
import com.increff.pos.util.UserPrincipal;

import io.swagger.annotations.ApiOperation;

@Controller
public class LoginController {


	@Autowired
	public UserDto userDto;

	@Autowired
	private InfoData info;
	
	@ApiOperation(value = "Logs in a user")
	@RequestMapping(path = "/session/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ModelAndView login(HttpServletRequest req, LoginForm f) throws ApiException {
		System.out.println("entered email is "+f.getEmail());
		UserData p = userDto.get(f.getEmail());
		System.out.println("entered email is "+f.getEmail());
		boolean authenticated = (p != null && Objects.equals(p.getPassword(), f.getPassword()));
		System.out.println("authentication done");
		if (!authenticated) {
			info.setMessage("Invalid username or password");
			return new ModelAndView("redirect:/site/login");
		}

		// Create authentication object
		Authentication authentication = convert(p);
		// Create new session
		HttpSession session = req.getSession(true);
		// Attach Spring SecurityContext to this new session
		SecurityUtil.createContext(session);
		// Attach Authentication object to the Security Context
		SecurityUtil.setAuthentication(authentication);

		return new ModelAndView("redirect:/ui/order");

	}

	@RequestMapping(path = "/session/logout", method = RequestMethod.GET)
	public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {
		request.getSession().invalidate();
		return new ModelAndView("redirect:/site/logout");
	}

	private static Authentication convert(UserData p) {
		// Create principal
		UserPrincipal principal = new UserPrincipal();
		principal.setEmail(p.getEmail());
		principal.setId(p.getId());
		principal.setRole(p.getRole());
		// Create Authorities
		ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(p.getRole()));
		// you can add more roles if required

		// Create Authentication
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, null,
				authorities);
		return token;
	}

}
