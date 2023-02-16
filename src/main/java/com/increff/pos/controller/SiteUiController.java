package com.increff.pos.controller;

import com.increff.pos.model.InfoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SiteUiController extends AbstractUiController {

	@Autowired
	private InfoData info;

	// WEBSITE PAGES
	@RequestMapping(value = "")
	public ModelAndView index() {
		return new ModelAndView("redirect:/site/login");
	}

	@RequestMapping(value = "/site/login")
	public ModelAndView login() {
		if(!(info.getEmail().equals("No email") || info.getEmail().equals(""))){
			return new ModelAndView("redirect:/ui/order");
		}
		return mav("login.html");
	}

	@RequestMapping(value = "/site/signUp")
	public ModelAndView signUp() {

		if(!(info.getEmail().equals("No email") || info.getEmail().equals(""))){
			return new ModelAndView("redirect:/ui/order");
		}
		return mav("signUp.html");
	}

	@RequestMapping(value = "/site/logout")
	public ModelAndView logout() {
		return mav("logout.html");
	}

}
